package kztproject.jp.splacounter.presentation.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kztproject.jp.splacounter.presentation.todo.model.EditingTodo
import kztproject.jp.splacounter.todo.application.CompleteTodoUseCase
import kztproject.jp.splacounter.todo.application.DeleteTodoUseCase
import kztproject.jp.splacounter.todo.application.GetTodoListUseCase
import kztproject.jp.splacounter.todo.application.UpdateTodoUseCase
import kztproject.jp.splacounter.todo.domain.Todo
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
