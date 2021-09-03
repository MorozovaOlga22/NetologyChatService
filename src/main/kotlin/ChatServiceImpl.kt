import java.lang.IllegalArgumentException
import kotlin.math.min

class ChatServiceImpl : ChatService {
    internal val chats = ArrayList<Chat>()
    internal var nextChatId = 0
    internal var nextMessageId = 0

    //Функции сервиса
    override fun getChats(userId: Int) =
        chats.filter { chat -> chat.isUserInChat(userId) }

    override fun getUnreadChatsCount(userId: Int) =
        getChats(userId)
            .filter { chat ->
                chat.messages.any { message -> message.isMessageUnreadByUser(userId) }
            }

    override fun readMessages(chatId: Int, userId: Int, firstMessageId: Int, messagesCount: Int): List<Message> {
        if (messagesCount <= 0) {
            throw IllegalArgumentException("The number of messages returned cannot be <= 0")
        }

        val chat = chats.getChat(chatId)
        chat.checkUserInChat(userId)

        val firstMessageIndex = chat.messages.indexOfFirst { message -> message.messageId == firstMessageId }
        if (firstMessageIndex < 0) {
            throw MessageNotFoundException(chatId, firstMessageId)
        }

        val lastMessageIndex = min(firstMessageIndex + messagesCount, chat.messages.size)
        val resultMessages = chat.messages.subList(firstMessageIndex, lastMessageIndex)

        resultMessages.forEach { message ->
            if (message.addresseeId == userId) {
                message.isRead = true
            }
        }
        return resultMessages
    }

    override fun createMessage(chatId: Int, senderId: Int, messageText: String) {
        val chat = chats.getChat(chatId)
        chat.checkUserInChat(senderId)

        val newMessage = Message(
            messageId = nextMessageId++,
            addresseeId = chat.getAddresseeId(senderId),
            senderId = senderId,
            text = messageText
        )

        chat.messages += newMessage
    }

    override fun changeMessage(chatId: Int, senderId: Int, messageId: Int, newMessageText: String) {
        val chat = chats.getChat(chatId)
        chat.checkUserInChat(senderId)

        val message = chat.messages
            .find { message -> message.messageId == messageId } ?: throw MessageNotFoundException(chatId, messageId)

        if (message.senderId != senderId) {
            throw UserNotSenderException(senderId, messageId)
        }

        message.text = newMessageText
    }

    override fun deleteMessage(chatId: Int, userId: Int, messageId: Int) {
        val chat = chats.getChat(chatId)
        chat.checkUserInChat(userId)

        val isMessageRemoved = chat.messages.removeIf { message -> message.messageId == messageId }
        if (!isMessageRemoved) {
            throw MessageNotFoundException(chatId, messageId)
        }

        if (chat.messages.isEmpty()) {
            deleteChat(chatId, userId)
        }
    }

    override fun createChat(senderId: Int, addresseeId: Int, messageText: String) {
        if (chats.any { chat -> chat.isUserInChat(senderId) && chat.isUserInChat(addresseeId) }) {
            throw ChatAlreadyExistsException(senderId, addresseeId)
        }

        val newChat = Chat(
            chatId = nextChatId++,
            user1Id = senderId,
            user2Id = addresseeId
        )
        chats.add(newChat)

        val newMessage = Message(
            messageId = nextMessageId++,
            addresseeId = addresseeId,
            senderId = senderId,
            text = messageText
        )
        newChat.messages.add(newMessage)
    }

    override fun deleteChat(chatId: Int, userId: Int) {
        val chat = chats.getChat(chatId)
        chat.checkUserInChat(userId)

        chats.remove(chat)
    }


    //Вспомогательные функции
    private fun Chat.isUserInChat(userId: Int) =
        user1Id == userId || user2Id == userId

    private fun Message.isMessageUnreadByUser(userId: Int) =
        !isRead && addresseeId == userId

    private fun ArrayList<Chat>.getChat(chatId: Int) =
        firstOrNull { chat -> chat.chatId == chatId } ?: throw ChatNotFoundException(chatId)

    private fun Chat.checkUserInChat(userId: Int) {
        if (!isUserInChat(userId)) {
            throw UserNotInChatException(userId, chatId)
        }
    }

    private fun Chat.getAddresseeId(senderId: Int) =
        if (user1Id == senderId) user2Id else user1Id
}