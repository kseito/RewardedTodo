package jp.kztproject.rewardedtodo.reward.application.usecase

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.reward.domain.model.Reward

interface GetRewardsUseCase {
    suspend fun execute(): List<Reward>

    suspend fun executeAsFlow(): Flow<List<Reward>>
}