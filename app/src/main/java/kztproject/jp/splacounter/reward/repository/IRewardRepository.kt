package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.database.model.Reward

interface IRewardRepository {

    fun createOrUpdate(reward: Reward)

    fun delete(reward: Reward)

    fun findBy(id: Int): Reward?

    fun findAll(): Array<Reward>
}