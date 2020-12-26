package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.data.reward.api.model.NumberOfTicket

interface GetPointUseCase {
    suspend fun execute(): jp.kztproject.rewardedtodo.data.reward.api.model.NumberOfTicket
}