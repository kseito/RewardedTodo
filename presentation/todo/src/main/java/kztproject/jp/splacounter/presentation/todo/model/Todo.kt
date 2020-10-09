package kztproject.jp.splacounter.presentation.todo.model

data class Todo(
        val id: Long,
        val name: String,
        val numberOfTicketsObtained: Float,
        val isRepeat: Boolean
)