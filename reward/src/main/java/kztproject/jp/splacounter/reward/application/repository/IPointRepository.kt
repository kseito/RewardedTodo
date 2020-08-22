package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint

interface IPointRepository {

    suspend fun loadPoint(): RewardPoint

    suspend fun consumePoint(userId: Long, additionalPoint: Int): RewardUser
}