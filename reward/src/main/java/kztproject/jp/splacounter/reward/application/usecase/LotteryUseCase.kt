package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward


interface LotteryUseCase {
    suspend fun execute(rewardEntities: List<Reward>): Reward?
}