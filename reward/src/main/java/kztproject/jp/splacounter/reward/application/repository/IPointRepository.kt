package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket

interface IPointRepository {

    suspend fun loadPoint(): NumberOfTicket

    suspend fun consumePoint(additionalPoint: Int): RewardUser
}