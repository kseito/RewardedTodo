package jp.kztproject.rewardedtodo.data.reward.repository

import jp.kztproject.rewardedtodo.data.reward.database.RewardDao
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class RewardRepositoryTest {

    private val rewardDao: RewardDao = mock()
    private val target: RewardRepository

    init {
        target = RewardRepository(rewardDao)
    }

    @Test
    fun insertReward() = runTest {
        val dummyReward = DummyCreator.createDummyRewardInput()
        target.createOrUpdate(dummyReward)

        verify(rewardDao, times(1)).insertReward(any())
    }

    @Test
    fun deleteReward() = runTest {
        val dummyReward = DummyCreator.createDummyReward()
        target.delete(dummyReward)

        verify(rewardDao, times(1)).deleteReward(RewardEntity.from(dummyReward))
    }

    @Test
    fun findBy() = runTest {
        target.findBy(1)

        verify(rewardDao, times(1)).findBy(1)
    }

    @Test
    fun findAll() = runTest {
        target.findAll()

        verify(rewardDao, times(1)).findAll()
    }

}
