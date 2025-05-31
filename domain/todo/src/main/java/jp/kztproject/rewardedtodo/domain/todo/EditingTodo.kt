package jp.kztproject.rewardedtodo.domain.todo

data class EditingTodo(
    var id: Long? = null,
    val todoistId: Long? = null,
    var name: String = "",
    private var numberOfTicketsObtained: Int = 0,
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
            this.id ?: 0,
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
        if (name.length > 500) {
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
