package kztproject.jp.splacounter.reward.application.repository

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

interface IRewardRepository {

    suspend fun createOrUpdate(rewardEntity: RewardEntity)

    suspend fun delete(rewardEntity: RewardEntity)

    suspend fun findBy(id: Int): RewardEntity?

    suspend fun findAll(): Array<RewardEntity>
}