package jp.kztproject.rewardedtodo.reward.application.repository

import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import jp.kztproject.rewardedtodo.reward.infrastructure.api.model.RewardUser

interface IPointRepository {

    suspend fun loadPoint(): NumberOfTicket

    suspend fun consumePoint(additionalPoint: Int): RewardUser
}