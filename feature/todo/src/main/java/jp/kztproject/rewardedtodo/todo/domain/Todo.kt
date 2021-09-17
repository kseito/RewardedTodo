package jp.kztproject.rewardedtodo.todo.domain

data class Todo(
        val id: Long?,
        val todoistId: Long?,
        var name: String,   //TODO divide model
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
) {
    fun hasTodoistId(): Boolean {
        return todoistId != null
    }
}