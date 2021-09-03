data class Chat(
    val chatId: Int,
    val user1Id: Int,
    val user2Id: Int,
    val messages: ArrayList<Message> = ArrayList()
)
