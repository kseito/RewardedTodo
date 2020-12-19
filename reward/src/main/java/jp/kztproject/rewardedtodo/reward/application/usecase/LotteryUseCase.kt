package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import jp.kztproject.rewardedtodo.reward.domain.model.RewardCollection


interface LotteryUseCase {
    suspend fun execute(rewards: RewardCollection): Reward?
}