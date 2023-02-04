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

    val todoList: LiveData<List<Todo>> = getTodoListUseCase.execute()
        .catch {
            it.printStackTrace()
            error.value = Result.failure(it)
        }
        .asLiveData()

    val error = MutableLiveData<Result<Unit>?>()

    fun initialize() {

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
        }
    }

    fun completeTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Default) {
            completeTodoUseCase.execute(todo)
        }
    }
}
