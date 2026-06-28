package jp.kztproject.rewardedtodo.presentation.todo

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodoListUseCase: GetTodoListUseCase,
    private val fetchTodoListUseCase: FetchTodoListUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val getApiTokenUseCase: GetApiTokenUseCase,
) : ViewModel() {

    private val _result = MutableStateFlow<Result<Unit>?>(null)
    val result: StateFlow<Result<Unit>?> = _result.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val hasAuthToken: StateFlow<Boolean> = getApiTokenUseCase.executeAsFlow()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    // プルリフレッシュによる手動同期を駆動するトリガー。
    // replay=0 にして、再購読時に過去のリフレッシュ信号が再生され二重fetchになるのを防ぐ。
    // extraBufferCapacity=1 で購読中の tryEmit を確実にバッファする。
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    // トークン変更を起点にネットワーク同期し、その後DBを継続観測する完全リアクティブな一覧
    val todoList: StateFlow<List<Todo>> = getApiTokenUseCase.executeAsFlow()
        .flatMapLatest { token ->
            refreshTrigger
                .onStart { emit(Unit) }
                .flatMapLatest {
                    flow {
                        if (token != null) {
                            _isRefreshing.update { true }
                            try {
                                fetchTodoListUseCase.execute()
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Timber.e(e)
                                _result.update { Result.failure(e) }
                            } finally {
                                _isRefreshing.update { false }
                            }
                        }
                        emitAll(getTodoListUseCase.execute())
                    }
                        // 1サイクルの失敗で共有ストリームを終わらせない（次のリフレッシュで復帰可能にする）
                        .catch { throwable ->
                            Timber.e(throwable)
                            _result.update { Result.failure(throwable) }
                        }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun refreshTodoList() {
        refreshTrigger.tryEmit(Unit)
    }

    fun updateTodo(todo: EditingTodo) {
        viewModelScope.launch {
            val newResult = updateTodoUseCase.execute(todo)
            _result.update { newResult }
        }
    }

    fun deleteTodo(todo: EditingTodo) {
        viewModelScope.launch {
            try {
                deleteTodoUseCase.execute(todo.toTodo())
                _result.update { Result.success(Unit) }
            } catch (e: Exception) {
                Timber.e(e)
                _result.update { Result.failure(e) }
            }
        }
    }

    fun completeTodo(todo: Todo) {
        viewModelScope.launch {
            completeTodoUseCase.execute(todo)
        }
    }

    fun clearResult() {
        _result.update { null }
    }
}
