package jp.kztproject.rewardedtodo.presentation.todo.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import jp.kztproject.rewardedtodo.todo.domain.Todo

data class EditingTodo(
        var id: Long? = null,
        var name: String = "",
        private var numberOfTicketsObtained: Float = 0f,
        private var isRepeat: Boolean = false
) : BaseObservable() {

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

    @Bindable
    fun getNumberOfTicketsObtained(): Float {
        return numberOfTicketsObtained
    }

    fun setNumberOfTicketsObtained(value: Float) {
        numberOfTicketsObtained = value
    }

    @Bindable
    fun getIsRepeat(): Boolean {
        return isRepeat
    }

    fun setIsRepeat(value: Boolean) {
        isRepeat = value
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