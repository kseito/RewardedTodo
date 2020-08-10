package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.infrastructure.api.RewardPointService
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint
import javax.inject.Inject

class PointRepository @Inject constructor(private val rewardPointClient: RewardPointService) : IPointRepository {

    override suspend fun loadPoint(userId: Long): RewardPoint {
        return rewardPointClient.getPoint(userId).await()
    }

    override suspend fun consumePoint(userId: Long, additionalPoint: Int): RewardUser =
            rewardPointClient.updatePoint(userId, -additionalPoint).await()
}