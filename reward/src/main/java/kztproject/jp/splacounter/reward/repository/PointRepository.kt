package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.reward.api.RewardPointService
import kztproject.jp.splacounter.reward.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint
import java.lang.NullPointerException
import javax.inject.Inject

class PointRepository @Inject constructor(private val rewardPointClient: RewardPointService) : IPointRepository {

    override suspend fun loadPoint(userId: Long): RewardPoint {
        return rewardPointClient.getPoint(userId).await()
    }

    override fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser> =
            rewardPointClient.updatePoint(userId, -additionalPoint)
}