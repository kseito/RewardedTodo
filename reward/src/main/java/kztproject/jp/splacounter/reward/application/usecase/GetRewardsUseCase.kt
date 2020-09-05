package kztproject.jp.splacounter.reward.application.usecase

import kotlinx.coroutines.flow.Flow
import kztproject.jp.splacounter.reward.domain.model.Reward

interface GetRewardsUseCase {
    suspend fun execute(): List<Reward>

    suspend fun executeAsFlow(): Flow<List<Reward>>
}