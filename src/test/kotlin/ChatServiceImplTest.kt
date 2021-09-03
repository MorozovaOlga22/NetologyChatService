import org.junit.Test

import org.junit.Assert.*
import java.lang.IllegalArgumentException

class ChatServiceImplTest {

    @Test
    fun getChats() {
        val chatServiceImpl = ChatServiceImpl()

        val userChat = createTestChat(0)
        chatServiceImpl.chats.add(userChat)

        val userChat1 = createTestChat(1)
        chatServiceImpl.chats.add(userChat1)

        val userChat2 =
            Chat(
                chatId = 2,
                user1Id = 1,
                user2Id = 0
            )
        chatServiceImpl.chats.add(userChat2)

        val notUserChat =
            Chat(
                chatId = 3,
                user1Id = 2,
                user2Id = 3
            )
        chatServiceImpl.chats.add(notUserChat)

        val expectedUserChats = listOf(userChat, userChat1, userChat2)

        val actualUserChats = chatServiceImpl.getChats(userId = 0)

        assertEquals(expectedUserChats, actualUserChats)
    }

    @Test
    fun getUnreadChatsCount() {
        val chatServiceImpl = ChatServiceImpl()

        val userChat = createTestChat(0)
        chatServiceImpl.chats.add(userChat)
        val unreadMessage = createTestMessage(0)
        userChat.messages.add(unreadMessage)

        val userChat1 = createTestChat(1)
        chatServiceImpl.chats.add(userChat1)
        val readMessage = createTestMessage(0)
        readMessage.isRead = true
        userChat1.messages.add(readMessage)

        val userChat2 = createTestChat(2)
        chatServiceImpl.chats.add(userChat2)
        val anotherUserMessage = Message(
            messageId = 0,
            senderId = 1,
            addresseeId = 0,
            text = "Any text"
        )
        userChat2.messages.add(anotherUserMessage)

        val expectedUserChats = listOf(userChat)

        val actualUserChats = chatServiceImpl.getUnreadChatsCount(userId = 1)

        assertEquals(expectedUserChats, actualUserChats)
    }

    @Test
    fun readMessages() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)
        val existingMessage1 = createTestMessage(1)
        existingChat.messages.add(existingMessage1)
        val existingMessage2 = createTestMessage(2)
        existingChat.messages.add(existingMessage2)
        val existingMessage3 = createTestMessage(3)
        existingChat.messages.add(existingMessage3)

        val expectedMessages = listOf(existingMessage1, existingMessage2)

        val actualMessages = chatServiceImpl.readMessages(
            chatId = 0,
            userId = 0,
            firstMessageId = 1,
            messagesCount = 2
        )

        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun readMessages_anotherUser() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)
        val existingMessage1 = createTestMessage(1)
        existingChat.messages.add(existingMessage1)
        val existingMessage2 = createTestMessage(2)
        existingChat.messages.add(existingMessage2)
        val existingMessage3 = createTestMessage(3)
        existingChat.messages.add(existingMessage3)

        val expectedMessage1 = createTestMessage(1)
        expectedMessage1.isRead = true
        val expectedMessage2 = createTestMessage(2)
        expectedMessage2.isRead = true

        val expectedMessages = listOf(expectedMessage1, expectedMessage2)

        val actualMessages = chatServiceImpl.readMessages(
            chatId = 0,
            userId = 1,
            firstMessageId = 1,
            messagesCount = 2
        )

        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun readMessages_manyMessages() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)
        val existingMessage1 = createTestMessage(1)
        existingChat.messages.add(existingMessage1)
        val existingMessage2 = createTestMessage(2)
        existingChat.messages.add(existingMessage2)
        val existingMessage3 = createTestMessage(3)
        existingChat.messages.add(existingMessage3)

        val expectedMessages = listOf(existingMessage1, existingMessage2, existingMessage3)

        val actualMessages = chatServiceImpl.readMessages(
            chatId = 0,
            userId = 0,
            firstMessageId = 1,
            messagesCount = 5
        )

        assertEquals(expectedMessages, actualMessages)
    }

    @Test(expected = IllegalArgumentException::class)
    fun readMessages_illegalArgumentException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.readMessages(
            chatId = 0,
            userId = 0,
            firstMessageId = 1,
            messagesCount = -1
        )
    }

    @Test(expected = ChatNotFoundException::class)
    fun readMessages_chatNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.readMessages(
            chatId = 0,
            userId = 0,
            firstMessageId = 1,
            messagesCount = 5
        )
    }

    @Test(expected = UserNotInChatException::class)
    fun readMessages_userNotInChatException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.readMessages(
            chatId = 0,
            userId = 2,
            firstMessageId = 1,
            messagesCount = 5
        )
    }

    @Test(expected = MessageNotFoundException::class)
    fun readMessages_messageNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.readMessages(
            chatId = 0,
            userId = 0,
            firstMessageId = 1,
            messagesCount = 5
        )
    }

    @Test
    fun createMessage() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)
        val expectedMessage = createTestMessage(0)

        chatServiceImpl.createMessage(
            chatId = 0,
            senderId = 0,
            messageText = "Any text"
        )

        assertEquals(1, existingChat.messages.size)
        assertEquals(expectedMessage, existingChat.messages[0])
        assertEquals(1, chatServiceImpl.nextMessageId)
    }

    @Test(expected = ChatNotFoundException::class)
    fun createMessage_chatNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.createMessage(
            chatId = 0,
            senderId = 0,
            messageText = "Any text"
        )
    }

    @Test(expected = UserNotInChatException::class)
    fun createMessage_userNotInChatException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.createMessage(
            chatId = 0,
            senderId = 2,
            messageText = "Any text"
        )
    }

    @Test
    fun changeMessage() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)
        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)

        val expectedMessage = createTestMessage(0)
        expectedMessage.text = "Another text"

        chatServiceImpl.changeMessage(
            chatId = 0,
            senderId = 0,
            messageId = 0,
            newMessageText = "Another text"
        )

        assertEquals(1, existingChat.messages.size)
        assertEquals(expectedMessage, existingChat.messages[0])
    }

    @Test(expected = ChatNotFoundException::class)
    fun changeMessage_chatNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.changeMessage(
            chatId = 0,
            senderId = 0,
            messageId = 0,
            newMessageText = "Another text"
        )
    }

    @Test(expected = UserNotInChatException::class)
    fun changeMessage_userNotInChatException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.changeMessage(
            chatId = 0,
            senderId = 2,
            messageId = 0,
            newMessageText = "Another text"
        )
    }

    @Test(expected = MessageNotFoundException::class)
    fun changeMessage_messageNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.changeMessage(
            chatId = 0,
            senderId = 0,
            messageId = 0,
            newMessageText = "Another text"
        )
    }

    @Test(expected = UserNotSenderException::class)
    fun changeMessage_userNotSenderException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)
        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)

        chatServiceImpl.changeMessage(
            chatId = 0,
            senderId = 1,
            messageId = 0,
            newMessageText = "Another text"
        )
    }

    @Test
    fun deleteMessage() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)
        val existingMessage = createTestMessage(0)
        existingChat.messages.add(existingMessage)

        chatServiceImpl.deleteMessage(
            chatId = 0,
            userId = 0,
            messageId = 0
        )

        assertEquals(0, existingChat.messages.size)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessage_chatNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.deleteMessage(
            chatId = 0,
            userId = 0,
            messageId = 0
        )
    }

    @Test(expected = UserNotInChatException::class)
    fun deleteMessage_userNotInChatException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.deleteMessage(
            chatId = 0,
            userId = 2,
            messageId = 0
        )
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteMessage_messageNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.deleteMessage(
            chatId = 0,
            userId = 0,
            messageId = 0
        )
    }

    @Test
    fun createChat() {
        val chatServiceImpl = ChatServiceImpl()

        val expectedChat = createTestChat(0)
        val expectedMessage = createTestMessage(0)
        expectedChat.messages.add(expectedMessage)

        chatServiceImpl.createChat(
            senderId = 0,
            addresseeId = 1,
            messageText = "Any text"
        )

        assertEquals(1, chatServiceImpl.chats.size)
        assertEquals(expectedChat, chatServiceImpl.chats[0])
        assertEquals(1, chatServiceImpl.nextChatId)
        assertEquals(1, chatServiceImpl.nextMessageId)
    }

    @Test(expected = ChatAlreadyExistsException::class)
    fun createChat_chatAlreadyExistsException() {
        val chatServiceImpl = ChatServiceImpl()

        val existingChat = createTestChat(0)
        chatServiceImpl.chats.add(existingChat)

        chatServiceImpl.createChat(
            senderId = 0,
            addresseeId = 1,
            messageText = "Any text"
        )
    }

    @Test
    fun deleteChat() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.chats.add(createTestChat(0))
        val notDeletedChat = createTestChat(1)
        chatServiceImpl.chats.add(notDeletedChat)

        chatServiceImpl.deleteChat(
            chatId = 0,
            userId = 0
        )

        assertEquals(1, chatServiceImpl.chats.size)
        assertEquals(notDeletedChat, chatServiceImpl.chats[0])
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_chatNotFoundException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.deleteChat(
            chatId = 0,
            userId = 0
        )
    }

    @Test(expected = UserNotInChatException::class)
    fun deleteChat_userNotInChatException() {
        val chatServiceImpl = ChatServiceImpl()

        chatServiceImpl.chats.add(createTestChat(0))

        chatServiceImpl.deleteChat(
            chatId = 0,
            userId = 2
        )
    }

    private fun createTestMessage(messageId: Int) =
        Message(
            messageId = messageId,
            addresseeId = 1,
            senderId = 0,
            text = "Any text"
        )

    private fun createTestChat(chatId: Int) =
        Chat(
            chatId = chatId,
            user1Id = 0,
            user2Id = 1
        )
}