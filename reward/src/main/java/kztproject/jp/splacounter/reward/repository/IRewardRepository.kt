package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.infrastructure.database.model.Reward

interface IRewardRepository {

    suspend fun createOrUpdate(reward: Reward)

    suspend fun delete(reward: Reward)

    suspend fun findBy(id: Int): Reward?

    suspend fun findAll(): Array<Reward>
}