package kztproject.jp.splacounter.reward.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import javax.inject.Inject

class RewardRepository @Inject constructor(private val rewardDao: RewardDao) : IRewardRepository {

    override suspend fun createOrUpdate(reward: Reward) {
        withContext(Dispatchers.IO) {
            rewardDao.insertReward(RewardEntity.from(reward))
        }
    }

    override suspend fun delete(reward: Reward) {
        withContext(Dispatchers.IO) {
            val rewardEntity = RewardEntity.from(reward)
            rewardDao.deleteReward(rewardEntity)
        }
    }

    override suspend fun findBy(id: Int): Reward? {
        return withContext(Dispatchers.IO) {
            rewardDao.findBy(id)?.convert()
        }
    }

    override suspend fun findAll(): List<Reward> {
        return withContext(Dispatchers.IO) {
            rewardDao.findAll()
                    .map { it.convert() }
        }
    }

}