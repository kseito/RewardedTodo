package jp.kztproject.rewardedtodo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * [HomeActivity]が持つ画面状態。
 *
 * 起動時にTodoist連携の有無を一度だけ判定し、最初に出す画面を決める。
 * 判定が終わるまで[startDestination]はnullで、その間はスプラッシュを表示し続ける。
 * 判定後の連携状態の変化は画面側のコールバックで扱うため、ここでは監視しない。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(getTodoistCredentialUseCase: GetTodoistCredentialUseCase) : ViewModel() {

    val startDestination: StateFlow<StartDestination?>
        field = MutableStateFlow<StartDestination?>(null)

    init {
        viewModelScope.launch {
            // 読み取れなければ未連携として扱う。ここで落とすと判定が終わらず起動できなくなる
            val credential = runCatching { getTodoistCredentialUseCase.execute() }
                .onFailure { Timber.e(it, "Failed to read the Todoist credential") }
                .getOrNull()
            startDestination.value = if (credential != null) StartDestination.HOME else StartDestination.AUTH
        }
    }
}

enum class StartDestination {
    AUTH,
    HOME,
}
