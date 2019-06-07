package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.api.model.RewardUser
import kztproject.jp.splacounter.reward.database.model.RewardPoint

interface IPointRepository {

    suspend fun loadPoint(userId: Long): RewardPoint

    suspend fun consumePoint(userId: Long, additionalPoint: Int): RewardUser
}