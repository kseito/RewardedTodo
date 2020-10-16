package kztproject.jp.splacounter.todo.domain

data class Todo(
        val id: Long,
        val name: String,
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
)