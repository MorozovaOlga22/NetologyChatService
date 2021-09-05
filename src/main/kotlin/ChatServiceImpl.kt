import java.lang.IllegalArgumentException
import kotlin.math.min

class ChatServiceImpl : ChatService {
    internal val chats = ArrayList<Chat>()
    internal var nextChatId = 0
    internal var nextMessageId = 0

    //Функции сервиса
    override fun getChats(userId: Int) =
        getUserChatsSequence(userId)
            .toList()

    override fun getUnreadChatsCount(userId: Int) =
        getUserChatsSequence(userId)
            .filter { chat ->
                chat.messages.any { message -> message.isMessageUnreadByUser(userId) }
            }
            .toList()

    override fun readMessages(chatId: Int, userId: Int, firstMessageId: Int, messagesCount: Int): List<Message> {
        if (messagesCount <= 0) {
            throw IllegalArgumentException("The number of messages returned cannot be <= 0")
        }

        val messages = chats.getUserChat(chatId, userId).messages

        val firstMessageIndex = messages.indexOfFirst { message -> message.messageId == firstMessageId }
            .let { firstMessageIndex ->
                if (firstMessageIndex < 0) {
                    throw MessageNotFoundException(chatId, firstMessageId)
                }
                firstMessageId
            }

        val lastMessageIndex = min(firstMessageIndex + messagesCount, messages.size)

        return messages.subList(firstMessageIndex, lastMessageIndex)
            .asSequence()
            .map { message ->
                if (message.addresseeId == userId) {
                    message.isRead = true
                }
                message
            }
            .toList()
    }

    override fun createMessage(chatId: Int, senderId: Int, messageText: String) {
        chats.getUserChat(chatId, senderId)
            .also { chat ->
                val newMessage = Message(
                    messageId = nextMessageId++,
                    addresseeId = chat.getAddresseeId(senderId),
                    senderId = senderId,
                    text = messageText
                )

                chat.messages += newMessage
            }
    }

    override fun changeMessage(chatId: Int, senderId: Int, messageId: Int, newMessageText: String) {
        chats.getUserChat(chatId, senderId).messages
            .asSequence()
            .filter { message -> message.messageId == messageId }
            .ifEmpty { throw MessageNotFoundException(chatId, messageId) }
            .first()
            .also { message ->
                if (message.senderId != senderId) {
                    throw UserNotSenderException(senderId, messageId)
                }
                message.text = newMessageText
            }
    }

    override fun deleteMessage(chatId: Int, userId: Int, messageId: Int) {
        val messages = chats.getUserChat(chatId, userId).messages

        messages.removeIf { message -> message.messageId == messageId }
            .also { isMessageRemoved ->
                if (!isMessageRemoved) {
                    throw MessageNotFoundException(chatId, messageId)
                }
            }

        if (messages.isEmpty()) {
            deleteChat(chatId, userId)
        }
    }

    override fun createChat(senderId: Int, addresseeId: Int, messageText: String) {
        if (chats.any { chat -> chat.isUserInChat(senderId) && chat.isUserInChat(addresseeId) }) {
            throw ChatAlreadyExistsException(senderId, addresseeId)
        }

        Chat(
            chatId = nextChatId++,
            user1Id = senderId,
            user2Id = addresseeId
        ).also { chat ->
            chat.messages.add(
                Message(
                    messageId = nextMessageId++,
                    addresseeId = addresseeId,
                    senderId = senderId,
                    text = messageText
                )
            )
            chats.add(chat)
        }
    }

    override fun deleteChat(chatId: Int, userId: Int) {
        val chat = chats.getUserChat(chatId, userId)
        chats.remove(chat)
    }


    //Вспомогательные функции
    private fun getUserChatsSequence(userId: Int) =
        chats.asSequence()
            .filter { chat -> chat.isUserInChat(userId) }

    private fun Chat.isUserInChat(userId: Int) =
        user1Id == userId || user2Id == userId

    private fun Message.isMessageUnreadByUser(userId: Int) =
        !isRead && addresseeId == userId

    private fun ArrayList<Chat>.getUserChat(chatId: Int, userId: Int) =
        filter { chat -> chat.chatId == chatId }
            .ifEmpty { throw ChatNotFoundException(chatId) }
            .first()
            .let { chat ->
                if (!chat.isUserInChat(userId)) {
                    throw UserNotInChatException(userId, chatId)
                }
                chat
            }

    private fun Chat.getAddresseeId(senderId: Int) =
        if (user1Id == senderId) user2Id else user1Id
}