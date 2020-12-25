package jp.kztproject.rewardedtodo.reward.application.repository

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import jp.kztproject.rewardedtodo.reward.domain.model.RewardInput

interface IRewardRepository {

    suspend fun createOrUpdate(reward: RewardInput)

    suspend fun delete(reward: Reward)

    suspend fun findBy(id: Int): Reward?

    suspend fun findAll(): List<Reward>

    suspend fun findAllAsFlow(): Flow<List<Reward>>
}