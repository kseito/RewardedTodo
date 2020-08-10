package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.infrastructure.database.model.Reward

interface LotteryUseCase {
    suspend fun execute(rewards: List<Reward>): Reward?
}