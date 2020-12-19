package jp.kztproject.rewardedtodo.presentation.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import jp.kztproject.rewardedtodo.presentation.todo.model.EditingTodo
import jp.kztproject.rewardedtodo.todo.application.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.todo.application.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.todo.application.GetTodoListUseCase
import jp.kztproject.rewardedtodo.todo.application.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.todo.domain.Todo
import javax.inject.Inject

class TodoListViewModel @Inject constructor(
        private val getTodoListUseCase: GetTodoListUseCase,
        private val updateTodoUseCase: UpdateTodoUseCase,
        private val deleteTodoUseCase: DeleteTodoUseCase,
        private val completeTodoUseCase: CompleteTodoUseCase
) : ViewModel() {

    private lateinit var callback: Callback
    val todoList: LiveData<List<Todo>> = getTodoListUseCase.execute().asLiveData()

    fun initialize(callback: Callback) {
        this.callback = callback
    }

    fun updateTodo(todo: EditingTodo) {
        viewModelScope.launch(Dispatchers.Default) {
            updateTodoUseCase.execute(todo.toTodo())

            callback.afterTodoUpdate()
        }
    }

    fun deleteTodo(todo: EditingTodo) {
        viewModelScope.launch(Dispatchers.Default) {
            deleteTodoUseCase.execute(todo.toTodo())

            callback.afterTodoUpdate()
        }
    }

    fun completeTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Default) {
            completeTodoUseCase.execute(todo)
        }
    }

    interface Callback {
        fun afterTodoUpdate()
    }
}
