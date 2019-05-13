package kztproject.jp.splacounter.reward.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.reward.database.RewardDao
import kztproject.jp.splacounter.reward.database.model.Reward
import javax.inject.Inject

class RewardRepository @Inject constructor(private val rewardDao: RewardDao): IRewardRepository {

    override suspend fun createOrUpdate(reward: Reward) {
        withContext(Dispatchers.IO) {
            rewardDao.insertReward(reward)
        }
    }

    override fun delete(reward: Reward) = rewardDao.deleteReward(reward)

    override suspend fun findBy(id: Int): Reward? {
        return withContext(Dispatchers.IO) {
            rewardDao.findBy(id)
        }
    }

    override fun findAll(): Array<Reward> = rewardDao.findAll()

}