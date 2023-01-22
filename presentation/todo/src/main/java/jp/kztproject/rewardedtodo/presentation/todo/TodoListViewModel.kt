package jp.kztproject.rewardedtodo.presentation.todo

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.todo.application.*
import jp.kztproject.rewardedtodo.todo.domain.EditingTodo
import jp.kztproject.rewardedtodo.todo.domain.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodoListUseCase: GetTodoListUseCase,
    private val fetchTodoListUseCase: FetchTodoListUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase
) : ViewModel() {

    private lateinit var callback: Callback
    val todoList: LiveData<List<Todo>> = getTodoListUseCase.execute()
        .catch {
            it.printStackTrace()
            callback.onError(it)
        }
        .asLiveData()

    val error = MutableLiveData<Result<Unit>?>()

    fun initialize(callback: Callback) {
        this.callback = callback

        viewModelScope.launch(Dispatchers.Default) {
            try {
                fetchTodoListUseCase.execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTodo(todo: EditingTodo) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = updateTodoUseCase.execute(todo)

            withContext(Dispatchers.Main) {
                error.value = result
            }
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

        fun onError(error: Throwable)
    }
}
