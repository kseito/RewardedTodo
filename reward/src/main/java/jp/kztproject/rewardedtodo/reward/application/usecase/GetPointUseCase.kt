package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket

interface GetPointUseCase {
    suspend fun execute(): NumberOfTicket
}