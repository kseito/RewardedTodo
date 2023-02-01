package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection


interface LotteryUseCase {
    suspend fun execute(rewards: RewardCollection): Result<Reward?>
}
