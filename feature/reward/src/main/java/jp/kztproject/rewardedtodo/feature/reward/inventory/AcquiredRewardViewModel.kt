package jp.kztproject.rewardedtodo.feature.reward.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.GetAcquiredRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.UseAcquiredRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryState(
    val used: Boolean = false,
    val items: List<AcquiredReward> = emptyList(),
    val cursor: String? = null,
    val loading: Boolean = false,
    val loaded: Boolean = false,
    val error: Throwable? = null,
)

@HiltViewModel
class AcquiredRewardViewModel @Inject constructor(
    private val getRewards: GetAcquiredRewardsUseCase,
    private val useReward: UseAcquiredRewardUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(InventoryState())
    val state = mutableState.asStateFlow()

    init {
        refresh()
    }

    fun select(used: Boolean) {
        if (state.value.loading || used == state.value.used) return
        mutableState.value = InventoryState(used = used)
        refresh()
    }

    fun refresh() = load(false)
    fun loadMore() = load(true)

    private fun load(more: Boolean) {
        val before = state.value
        if (before.loading || (more && before.cursor == null)) return
        mutableState.value = before.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val page = getRewards.execute(before.used, if (more) before.cursor else null)
                mutableState.value = before.copy(
                    items = (if (more) before.items + page.items else page.items).distinctBy { it.id },
                    cursor = page.nextCursor,
                    loaded = true,
                )
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (e: Exception) {
                mutableState.value = before.copy(error = e)
            }
        }
    }

    fun use(id: AcquiredReward.Id) {
        val before = state.value
        if (before.loading) return
        mutableState.value = before.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                useReward.execute(id)
                mutableState.value = before.copy(items = before.items.filterNot { it.id == id })
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (e: Exception) {
                mutableState.value = before.copy(error = e)
            }
        }
    }
}
