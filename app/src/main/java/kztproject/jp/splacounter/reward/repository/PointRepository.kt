package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.reward.api.RewardListService
import kztproject.jp.splacounter.reward.database.model.RewardPoint
import javax.inject.Inject

class PointRepository @Inject constructor(private val rewardListClient: RewardListService) : IPointRepository {

    override fun loadPoint(userId: Long): Single<RewardPoint> = rewardListClient.getPoint(userId)

    override fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser> =
            rewardListClient.updatePoint(userId, -additionalPoint)
}