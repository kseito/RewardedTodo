package jp.kztproject.rewardedtodo.data.reward.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.data.reward.database.RewardDao
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
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