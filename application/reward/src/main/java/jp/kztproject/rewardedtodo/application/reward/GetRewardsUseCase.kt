package jp.kztproject.rewardedtodo.application.reward

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.domain.reward.Reward

interface GetRewardsUseCase {
    suspend fun execute(): List<Reward>

    suspend fun executeAsFlow(): Flow<List<Reward>>
}