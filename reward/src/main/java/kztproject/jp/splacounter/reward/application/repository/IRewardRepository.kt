package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.domain.model.Reward

interface IRewardRepository {

    suspend fun createOrUpdate(reward: Reward)

    suspend fun delete(reward: Reward)

    suspend fun findBy(id: Int): Reward?

    suspend fun findAll(): List<Reward>
}