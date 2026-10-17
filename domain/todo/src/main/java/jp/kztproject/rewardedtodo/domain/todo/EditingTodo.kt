package jp.kztproject.rewardedtodo.domain.todo

data class EditingTodo(
    val todoistId: String,
    var name: String = "",
    private var numberOfTicketsObtained: Int = 0,
    private var isRepeat: Boolean = false,
) {

    companion object {
        fun from(todo: Todo): EditingTodo = EditingTodo(
            todo.todoistId,
            todo.name,
            todo.numberOfTicketsObtained,
            todo.isRepeat,
        )
    }

    fun toTodo(): Todo = Todo(
        this.todoistId,
        this.name,
        this.numberOfTicketsObtained,
        this.isRepeat,
    )

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
