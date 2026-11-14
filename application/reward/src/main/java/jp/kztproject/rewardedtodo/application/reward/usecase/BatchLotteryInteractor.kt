package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class BatchLotteryInteractor @Inject constructor(private val repository: ILotteryRepository) : BatchLotteryUseCase {
    override suspend fun execute(count: Int): Result<BatchLotteryResult> = try {
        val result = repository.draw(count)
        Result.success(BatchLotteryResult(result.wonRewards, result.missCount))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
