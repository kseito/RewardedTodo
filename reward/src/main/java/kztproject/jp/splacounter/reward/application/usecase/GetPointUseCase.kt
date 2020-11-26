package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket

interface GetPointUseCase {
    suspend fun execute(): NumberOfTicket
}