package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.presentation.detail.model.RewardInput

interface IRewardRepository {

    suspend fun createOrUpdate(reward: RewardInput)

    suspend fun delete(reward: Reward)

    suspend fun findBy(id: Int): Reward?

    suspend fun findAll(): List<Reward>
}