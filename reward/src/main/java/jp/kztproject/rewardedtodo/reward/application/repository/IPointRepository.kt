package jp.kztproject.rewardedtodo.reward.application.repository

import jp.kztproject.rewardedtodo.reward.infrastructure.api.model.RewardUser
import jp.kztproject.rewardedtodo.reward.infrastructure.database.model.NumberOfTicket

interface IPointRepository {

    suspend fun loadPoint(): NumberOfTicket

    suspend fun consumePoint(additionalPoint: Int): RewardUser
}