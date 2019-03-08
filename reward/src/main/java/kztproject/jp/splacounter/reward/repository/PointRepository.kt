package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.reward.api.RewardPointService
import kztproject.jp.splacounter.reward.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint
import javax.inject.Inject

class PointRepository @Inject constructor(private val rewardPointClient: RewardPointService) : IPointRepository {

    override fun loadPoint(userId: Long): Single<RewardPoint> = rewardPointClient.getPoint(userId)

    override fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser> =
            rewardPointClient.updatePoint(userId, -additionalPoint)
}