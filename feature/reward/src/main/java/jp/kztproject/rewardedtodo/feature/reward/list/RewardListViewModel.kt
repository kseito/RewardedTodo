package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.model.error.OverMaxRewardsException
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.application.reward.usecase.RecoverLotteryUseCase
import kotlinx.coroutines.CancellationException
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val lotteryUseCase: LotteryUseCase,
    private val batchLotteryUseCase: BatchLotteryUseCase,
    private val getRewardsUseCase: GetRewardsUseCase,
    private val getPointUseCase: GetPointUseCase,
    private val saveRewardUseCase: SaveRewardUseCase,
    private val deleteRewardUseCase: DeleteRewardUseCase,
    private val recoverLotteryUseCase: RecoverLotteryUseCase,
) : ViewModel() {

    private val rewardsState = MutableStateFlow<List<Reward>>(emptyList())
    val rewardList: StateFlow<List<Reward>> = rewardsState
    val isSaving = MutableStateFlow(false)
    val hasPendingLottery = MutableStateFlow(false)
    val isRecovering = MutableStateFlow(false)

    private suspend fun reloadRewards(reportError: Boolean = true) {
        try {
            rewardsState.value = getRewardsUseCase.execute()
        } catch (
            e: CancellationException,
        ) {
            throw e
        } catch (e: Exception) {
            if (reportError) result.value = Result.failure(e)
        }
    }

    // ポイント再取得を駆動するトリガー。抽選後など、値の変化を能動的に反映したいときに発火する。
    // ネットワークモードの getNumberOfTicket() はワンショットFlowのため、購読しっぱなしでは
    // 抽選後に再emitされない。トリガーで再フェッチして両モードに対応する。
    // replay=0 にして再購読時の信号再生による二重フェッチを防ぎ、extraBufferCapacity=1 で
    // 購読中の tryEmit を確実にバッファする。
    private val pointRefreshTrigger = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    val rewardPoint: StateFlow<Int> = pointRefreshTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            // catch を flatMapLatest 内に置き、1回の取得失敗で共有ストリームを終わらせない。
            // 外側に置くと例外で StateFlow の収集が止まり、以降のトリガーで復帰できなくなる。
            flow { emitAll(getPointUseCase.execute()) }
                .map { it.value }
                .onEach { isRefreshing.value = false }
                .catch {
                    result.value = Result.failure(it)
                    isRefreshing.value = false
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0,
        )
    val result: StateFlow<Result<Unit>?>
        field = MutableStateFlow<Result<Unit>?>(null)
    val obtainedReward: StateFlow<Result<AcquiredReward?>?>
        field = MutableStateFlow<Result<AcquiredReward?>?>(null)
    val batchLotteryResult: StateFlow<Result<BatchLotteryResult>?>
        field = MutableStateFlow<Result<BatchLotteryResult>?>(null)
    val isSingleLottering: StateFlow<Boolean>
        field = MutableStateFlow(false)
    val isBatchLottering: StateFlow<Boolean>
        field = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        field = MutableStateFlow(false)

    /**
     * 画面を表示したときにチケット残数を引き直す。
     *
     * サーバー経由の取得はワンショットで、Todoタブでタスクを完了しても自動では反映されない。
     * ユーザーの操作ではないためインジケータは出さない。
     */
    fun refreshPointSilently() {
        refresh()
        recoverLottery()
    }

    fun refresh() {
        if (isRefreshing.value) return
        isRefreshing.value = true
        viewModelScope.launch {
            try {
                reloadRewards()
                pointRefreshTrigger.tryEmit(Unit)
            } finally {
                isRefreshing.value = false
            }
        }
    }

    fun recoverLottery() {
        if (isRecovering.value || isSingleLottering.value || isBatchLottering.value) return
        isRecovering.value = true
        viewModelScope.launch {
            try {
                val recovered = recoverLotteryUseCase.execute()
                hasPendingLottery.value = false
                if (recovered != null) {
                    if (recovered.count == 1) {
                        obtainedReward.value = Result.success(recovered.wonRewards.singleOrNull())
                    } else {
                        batchLotteryResult.value =
                            Result.success(BatchLotteryResult(recovered.wonRewards, recovered.missCount))
                    }
                    reloadRewards()
                    pointRefreshTrigger.tryEmit(Unit)
                }
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (
                e: Exception,
            ) {
                hasPendingLottery.value = true
                result.value = Result.failure(e)
            } finally {
                isRecovering.value = false
            }
        }
    }

    fun startLottery() {
        if (isSingleLottering.value || isBatchLottering.value || isRecovering.value || hasPendingLottery.value) return
        isSingleLottering.value = true
        viewModelScope.launch {
            try {
                obtainedReward.value = lotteryUseCase.execute()
                hasPendingLottery.value = obtainedReward.value?.isFailure == true
                reloadRewards()
                pointRefreshTrigger.tryEmit(Unit)
            } finally {
                isSingleLottering.value = false
            }
        }
    }

    fun resetObtainedReward() {
        obtainedReward.value = null
    }

    fun startBatchLottery(count: Int = BatchLotteryResult.DEFAULT_COUNT) {
        if (isSingleLottering.value || isBatchLottering.value || isRecovering.value || hasPendingLottery.value) return
        isBatchLottering.value = true
        viewModelScope.launch {
            try {
                batchLotteryResult.value = batchLotteryUseCase.execute(count)
                hasPendingLottery.value = batchLotteryResult.value?.isFailure == true
                reloadRewards()
                pointRefreshTrigger.tryEmit(Unit)
            } finally {
                isBatchLottering.value = false
            }
        }
    }

    fun resetBatchLotteryResult() {
        batchLotteryResult.value = null
    }

    fun validateRewards(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val rewards = rewardList.value
            if (rewards.size >= RewardCollection.MAX) {
                // TODO Create error property to show this error
                obtainedReward.value = Result.failure(OverMaxRewardsException())
            } else {
                onSuccess()
            }
        }
    }

    fun saveReward(reward: RewardInput) {
        if (isSaving.value) return
        isSaving.value = true
        viewModelScope.launch {
            try {
                result.value = saveRewardUseCase.execute(reward)
                if (result.value?.isSuccess == true) reloadRewards(reportError = false)
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (e: Exception) {
                result.value = Result.failure(e)
            } finally {
                isSaving.value = false
            }
        }
    }

    fun clearResult() {
        result.value = null
    }

    fun deleteReward(reward: Reward) {
        if (isSaving.value) return
        isSaving.value = true
        viewModelScope.launch {
            try {
                deleteRewardUseCase.execute(reward)
                result.value = Result.success(Unit)
                reloadRewards(reportError = false)
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (e: Exception) {
                result.value = Result.failure(e)
            } finally {
                isSaving.value = false
            }
        }
    }
}
