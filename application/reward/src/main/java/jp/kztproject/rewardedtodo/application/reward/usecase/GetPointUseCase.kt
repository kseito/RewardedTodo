package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import kotlinx.coroutines.flow.Flow

interface GetPointUseCase {
    suspend fun execute(): Flow<NumberOfTicket>
}
