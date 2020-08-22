package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

interface IRewardRepository {

    suspend fun createOrUpdate(rewardEntity: RewardEntity)

    suspend fun delete(rewardEntity: Reward)

    suspend fun findBy(id: Int): RewardEntity?

    suspend fun findAll(): List<RewardEntity>
}