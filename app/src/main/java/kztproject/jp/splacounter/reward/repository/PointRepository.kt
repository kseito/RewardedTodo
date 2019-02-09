package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.reward.api.RewardListClient
import javax.inject.Inject

class PointRepository @Inject constructor(private val rewardListClient: RewardListClient): IPointRepository {

    override fun loadPoint(userId: Long): Single<Int> = rewardListClient.getPoint(userId)

    override fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser> =
            rewardListClient.consumePoint(userId, -additionalPoint)
}