class ChatNotFoundException(chatId: Int) :
    RuntimeException("Chat with id $chatId not found")

class UserNotInChatException(userId: Int, chatId: Int) :
    RuntimeException("User with id $userId not included in chat with id $chatId")

class MessageNotFoundException(chatId: Int, messageId: Int) :
    RuntimeException("Message with id $messageId not found in chat with id $chatId")

class ChatAlreadyExistsException(user1Id: Int, user2Id: Int) :
    RuntimeException("Chat with users $user1Id and $user2Id already exists")

class UserNotSenderException(userId: Int, messageId: Int) :
    RuntimeException("User with id $userId is not a sender of message with id $messageId")