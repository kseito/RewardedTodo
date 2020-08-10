package kztproject.jp.splacounter.reward.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
import kztproject.jp.splacounter.reward.database.model.Reward
import javax.inject.Inject

class RewardRepository @Inject constructor(private val rewardDao: RewardDao): IRewardRepository {

    override suspend fun createOrUpdate(reward: Reward) {
        withContext(Dispatchers.IO) {
            rewardDao.insertReward(reward)
        }
    }

    override suspend fun delete(reward: Reward) {
        withContext(Dispatchers.IO) {
            rewardDao.deleteReward(reward)
        }
    }

    override suspend fun findBy(id: Int): Reward? {
        return withContext(Dispatchers.IO) {
            rewardDao.findBy(id)
        }
    }

    override suspend fun findAll(): Array<Reward> {
        return withContext(Dispatchers.IO) {
            rewardDao.findAll()
        }
    }

}