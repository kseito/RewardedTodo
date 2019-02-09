package kztproject.jp.splacounter.reward.repository

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser

interface IPointRepository {

    fun loadPoint(userId: Long): Single<Int>

    fun consumePoint(userId: Long, additionalPoint: Int): Single<RewardUser>
}