package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult

interface BatchLotteryUseCase {
    suspend fun execute(count: Int): Result<BatchLotteryResult>
}
