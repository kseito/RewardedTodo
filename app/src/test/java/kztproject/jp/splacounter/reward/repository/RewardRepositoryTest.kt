package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.database.RewardDao
import org.junit.Test

class RewardRepositoryTest {

    private val rewardDao: RewardDao = mock()
    private val target: RewardRepository

    init {
        target = RewardRepository(rewardDao)
    }

    @Test
    fun insertReward() {
        val dummyReward = DummyCreator.createDummyReward()
        target.createOrUpdate(dummyReward)

        verify(rewardDao, times(1)).insertReward(dummyReward)
    }

    @Test
    fun deleteReward() {
        val dummyReward = DummyCreator.createDummyReward()
        target.delete(dummyReward)

        verify(rewardDao, times(1)).deleteReward(dummyReward)
    }

    @Test
    fun findBy() {
        target.findBy(1)

        verify(rewardDao, times(1)).findBy(1)
    }

    @Test
    fun findAll() {
        target.findAll()

        verify(rewardDao, times(1)).findAll()
    }

}