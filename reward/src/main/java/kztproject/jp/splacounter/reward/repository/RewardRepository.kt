package kztproject.jp.splacounter.reward.repository

import kztproject.jp.splacounter.reward.database.RewardDao
import kztproject.jp.splacounter.reward.database.model.Reward
import javax.inject.Inject

class RewardRepository @Inject constructor(private val rewardDao: RewardDao): IRewardRepository {
    override fun createOrUpdate(reward: Reward) = rewardDao.insertReward(reward)

    override fun delete(reward: Reward) = rewardDao.deleteReward(reward)

    override fun findBy(id: Int): Reward? = rewardDao.findBy(id)

    override fun findAll(): Array<Reward> = rewardDao.findAll()

}