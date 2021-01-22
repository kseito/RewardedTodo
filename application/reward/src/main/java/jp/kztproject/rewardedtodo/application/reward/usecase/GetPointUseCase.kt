package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket

interface GetPointUseCase {
    suspend fun execute(): NumberOfTicket
}