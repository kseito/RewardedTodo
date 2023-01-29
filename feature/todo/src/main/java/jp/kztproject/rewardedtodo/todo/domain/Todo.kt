package jp.kztproject.rewardedtodo.todo.domain

data class Todo(
        val id: Long?,
        val todoistId: Long?,
        var name: String,   //TODO divide model
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
) {
    companion object {
        const val DEFAULT_NUMBER_OF_TICHKET = 1.0f
    }
    fun hasTodoistId(): Boolean {
        return todoistId != null
    }
}
