package kztproject.jp.splacounter.presentation.todo.model

import kztproject.jp.splacounter.todo.domain.Todo

data class EditingTodo(
        var id: Long? = null,
        var name: String = "",
        var numberOfTicketsObtained: Float = 0f,
        var isRepeat: Boolean = false
) {
    companion object {
        fun from(todo: Todo): EditingTodo {
            return EditingTodo(
                    todo.id,
                    todo.name,
                    todo.numberOfTicketsObtained,
                    todo.isRepeat
            )
        }
    }

    fun toTodo(): Todo {
        return Todo(
                this.id,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }
}