package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.reward.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint

interface IPointRepository {

    suspend fun loadPoint(userId: Long): RewardPoint

    fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser>
}