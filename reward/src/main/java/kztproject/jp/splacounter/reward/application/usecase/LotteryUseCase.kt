package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardCollection


interface LotteryUseCase {
    suspend fun execute(rewards: RewardCollection): Reward?
}