data class Message(
    val messageId: Int,
    val addresseeId: Int,
    val senderId: Int, //избыточная информация, но так удобнее работать с сообщениями
    var text: String,
    var isRead: Boolean = false
)