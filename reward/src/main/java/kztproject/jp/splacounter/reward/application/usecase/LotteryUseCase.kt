package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

interface LotteryUseCase {
    suspend fun execute(rewardEntities: List<RewardEntity>): RewardEntity?
}