package kztproject.jp.splacounter.todo.domain

data class Todo(
        val id: Long,
        var name: String,   //TODO divide model
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
)