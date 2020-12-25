package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.infrastructure.database.model.NumberOfTicket

interface GetPointUseCase {
    suspend fun execute(): NumberOfTicket
}