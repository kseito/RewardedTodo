package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint

interface GetPointUseCase {
    suspend fun execute(): RewardPoint
}