package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class LotteryInteractor @Inject constructor(private val repository: ILotteryRepository) : LotteryUseCase {
    override suspend fun execute(): Result<AcquiredReward?> = try {
        val result = repository.draw(1)
        Result.success(result.wonRewards.singleOrNull())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
