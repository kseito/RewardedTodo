package jp.kztproject.rewardedtodo.presentation.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodoListUseCase: GetTodoListUseCase,
    private val fetchTodoListUseCase: FetchTodoListUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val getApiTokenUseCase: GetApiTokenUseCase,
) : ViewModel() {

    val todoList: LiveData<List<Todo>> = getTodoListUseCase.execute()
        .catch {
            it.printStackTrace()
            result.value = Result.failure(it)
        }
        .asLiveData()

    val result = MutableLiveData<Result<Unit>?>()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _hasAuthToken = MutableStateFlow(false)
    val hasAuthToken = _hasAuthToken.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            checkAuthToken()

            try {
                if (_hasAuthToken.value) {
                    fetchTodoListUseCase.execute()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result.value = Result.failure(e)
            }
        }
    }

    fun refreshTodoList() {
        viewModelScope.launch(Dispatchers.Default) {
            _isRefreshing.update { true }
            checkAuthToken()

            try {
                if (_hasAuthToken.value) {
                    fetchTodoListUseCase.execute()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result.value = Result.failure(e)
            }
            _isRefreshing.update { false }
        }
    }

    fun updateTodo(todo: EditingTodo) {
        viewModelScope.launch(Dispatchers.Default) {
            val newResult = updateTodoUseCase.execute(todo)

            withContext(Dispatchers.Main) {
                result.value = newResult
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

    private suspend fun checkAuthToken() {
        val token = getApiTokenUseCase.execute()
        _hasAuthToken.update { token != null }
    }
}
