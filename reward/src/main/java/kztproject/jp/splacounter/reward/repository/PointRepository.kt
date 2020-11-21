package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.infrastructure.api.RewardPointService
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class PointRepository @Inject constructor(
        private val rewardPointClient: RewardPointService,
        private val preferences: PrefsWrapper
) : IPointRepository {

    override suspend fun loadPoint(): NumberOfTicket {
        return rewardPointClient.getPoint(preferences.userId).await()
    }

    override suspend fun consumePoint(additionalPoint: Int): RewardUser =
            rewardPointClient.updatePoint(preferences.userId, -additionalPoint).await()
}