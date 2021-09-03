import java.lang.IllegalArgumentException

interface ChatService {
    /**
     * Получить информацию о количестве чатов указанного пользователя
     *
     * @param userId -- id пользователя
     */
    fun getChats(userId: Int): List<Chat>

    /**
     * Получить информацию о количестве непрочитанных чатов указанного пользователя (количество чатов,
     * в каждом из которых у данного пользователя есть хотя бы одно непрочитанное сообщение)
     *
     * @param userId -- id пользователя
     */
    fun getUnreadChatsCount(userId: Int): List<Chat>

    /**
     * Получить сообщения из чата.
     * Сообщения, в которых пользователь является адресатом, становятся прочитанными
     *
     * @param chatId -- id чата
     * @param userId -- id пользователя
     * @param firstMessageId -- id сообщения, начиная с которого нужно подгрузить более новые
     * @param messagesCount -- количество сообщений, которые нужно получить. Натуральное число.
     * Если нашлось меньше сообщений, вернутся только они
     *
     * @exception IllegalArgumentException -- если параметр messagesCount <= 0
     * @exception ChatNotFoundException -- если чат не найден
     * @exception UserNotInChatException -- если пользователь не является участником чата
     * @exception MessageNotFoundException -- если сообщение с firstMessageId не найдено
     */
    fun readMessages(chatId: Int, userId: Int, firstMessageId: Int, messagesCount: Int): List<Message>

    /**
     * Создать сообщение в существующем чате
     *
     * @param chatId -- id чата
     * @param senderId -- id отправителя сообщения
     * @param messageText -- текст сообщения
     *
     * @exception ChatNotFoundException -- если чат не найден
     * @exception UserNotInChatException -- если пользователь не является участником чата
     */
    fun createMessage(chatId: Int, senderId: Int, messageText: String)

    /**
     * Изменить сообщение в чате.
     * Пользователь может менять только те сообщения, в которых он является отправителем
     *
     * @param chatId -- id чата
     * @param senderId -- id отправителя сообщения
     * @param messageId -- id сообщения
     * @param newMessageText -- новый текст сообщения
     *
     * @exception ChatNotFoundException -- если чат не найден
     * @exception UserNotInChatException -- если пользователь не является участником чата
     * @exception MessageNotFoundException -- если сообщение не найдено
     * @exception UserNotSenderException -- если пользователь не является отправителем сообщения
     */
    fun changeMessage(chatId: Int, senderId: Int, messageId: Int, newMessageText: String)

    /**
     * Удалить сообщение из чата.
     * Пользователь может удалять и свои, и чужие сообщения
     *
     * @param chatId -- id чата
     * @param userId -- id пользователя
     * @param messageId -- id сообщения
     *
     * @exception ChatNotFoundException -- если чат не найден
     * @exception UserNotInChatException -- если пользователь не является участником чата
     * @exception MessageNotFoundException -- если сообщение не найдено
     */
    fun deleteMessage(chatId: Int, userId: Int, messageId: Int)

    /**
     * Создать чат
     *
     * @param senderId -- id отправителя сообщения
     * @param addresseeId -- id адресата
     * @param messageText -- текст сообщения
     *
     * @exception ChatAlreadyExistsException -- если чат с этими пользователями уже существует
     *
     */
    fun createChat(senderId: Int, addresseeId: Int, messageText: String)

    /**
     * Удалить чат
     *
     * @param chatId -- id чата
     * @param userId -- id пользователя
     *
     * @exception ChatNotFoundException -- если чат не найден
     * @exception UserNotInChatException -- если пользователь не является участником чата
     */
    fun deleteChat(chatId: Int, userId: Int)
}