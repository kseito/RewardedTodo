package jp.kztproject.rewardedtodo.domain.todo

data class Todo(
    val todoistId: String,
    var name: String, // TODO divide model
    val numberOfTicketsObtained: Int,
    val isRepeat: Boolean,
) {
    companion object {
        const val DEFAULT_NUMBER_OF_TICHKET = 1
    }
}
