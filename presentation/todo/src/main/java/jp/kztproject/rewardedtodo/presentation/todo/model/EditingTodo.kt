package jp.kztproject.rewardedtodo.presentation.todo.model

import jp.kztproject.rewardedtodo.todo.domain.Todo

data class EditingTodo(
    var id: Long? = null,
    val todoistId: Long? = null,
    var name: String = "",
    private var numberOfTicketsObtained: Float = 0f,
    private var isRepeat: Boolean = false
) {

    companion object {
        fun from(todo: Todo): EditingTodo {
            return EditingTodo(
                todo.id,
                todo.todoistId,
                todo.name,
                todo.numberOfTicketsObtained,
                todo.isRepeat
            )
        }
    }

    fun toTodo(): Todo {
        return Todo(
            this.id,
            this.todoistId,
            this.name,
            this.numberOfTicketsObtained,
            this.isRepeat
        )
    }

    fun validate() {
        if (name.isEmpty()) {
            throw NameEmptyException()
        }
        if (name.length > 100) {
            throw NameLengthTooLongException()
        }
        if (numberOfTicketsObtained <= 0) {
            throw InvalidNumberOfTicketsException()
        }
        if (numberOfTicketsObtained > 100) {
            throw InvalidNumberOfTicketsException()
        }
    }
}
