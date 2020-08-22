package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.infrastructure.api.RewardPointService
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class PointRepository @Inject constructor(
        private val rewardPointClient: RewardPointService,
        private val preferences: PrefsWrapper
) : IPointRepository {

    override suspend fun loadPoint(): RewardPoint {
        return rewardPointClient.getPoint(preferences.userId).await()
    }

    override suspend fun consumePoint(userId: Long, additionalPoint: Int): RewardUser =
            rewardPointClient.updatePoint(userId, -additionalPoint).await()
}