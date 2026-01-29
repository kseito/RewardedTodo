package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection

interface BatchLotteryUseCase {
    suspend fun execute(rewards: RewardCollection, count: Int): Result<BatchLotteryResult>
}
