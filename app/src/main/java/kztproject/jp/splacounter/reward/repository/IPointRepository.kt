package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint

interface IPointRepository {

    fun loadPoint(userId: Long): Single<RewardPoint>

    fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser>
}