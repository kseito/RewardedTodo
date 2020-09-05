package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import org.junit.Test

class RewardRepositoryTest {

    private val rewardDao: RewardDao = mock()
    private val target: RewardRepository

    init {
        target = RewardRepository(rewardDao)
    }

    @Test
    fun insertReward() {
        val dummyReward = DummyCreator.createDummyRewardInput()
        runBlocking {
            target.createOrUpdate(dummyReward)

            verify(rewardDao, times(1)).insertReward(any())
        }
    }

    @Test
    fun deleteReward() {
        runBlocking {
            val dummyReward = DummyCreator.createDummyReward()
            target.delete(dummyReward)

            verify(rewardDao, times(1)).deleteReward(RewardEntity.from(dummyReward))
        }
    }

    @Test
    fun findBy() {
        runBlocking {
            target.findBy(1)

            verify(rewardDao, times(1)).findBy(1)
        }
    }

    @Test
    fun findAll() {
        runBlocking {
            target.findAll()

            verify(rewardDao, times(1)).findAll()
        }
    }

}