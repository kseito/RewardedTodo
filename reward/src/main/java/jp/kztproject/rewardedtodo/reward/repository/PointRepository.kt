package jp.kztproject.rewardedtodo.reward.repository

import jp.kztproject.rewardedtodo.domain.reward.repository.IPointRepository
import jp.kztproject.rewardedtodo.domain.reward.RewardUser
import jp.kztproject.rewardedtodo.data.reward.api.RewardPointService
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class PointRepository @Inject constructor(
        private val rewardPointClient: RewardPointService,
        private val preferences: PrefsWrapper
) : IPointRepository {

    override suspend fun loadPoint(): NumberOfTicket {
        return rewardPointClient.getPoint(preferences.userId)
                .await()
                .convert()
    }

    override suspend fun consumePoint(additionalPoint: Int): RewardUser =
            rewardPointClient.updatePoint(preferences.userId, -additionalPoint).await()
}