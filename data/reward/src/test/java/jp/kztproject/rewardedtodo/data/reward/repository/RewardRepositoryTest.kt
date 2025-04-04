package jp.kztproject.rewardedtodo.data.reward.repository

import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.reward.database.RewardDao
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RewardRepositoryTest {

    private val rewardDao: RewardDao = mockk(relaxed = true)
    private val target: RewardRepository = RewardRepository(rewardDao)

    @Test
    fun insertReward() = runTest {
        val dummyReward = DummyCreator.createDummyRewardInput()
        target.createOrUpdate(dummyReward)

        coVerify(exactly = 1) { rewardDao.insertReward(any()) }
    }

    @Test
    fun deleteReward() = runTest {
        val dummyReward = DummyCreator.createDummyReward()
        target.delete(dummyReward)

        coVerify(exactly = 1) { rewardDao.deleteReward(RewardEntity.from(dummyReward)) }
    }

    @Test
    fun findBy() = runTest {
        target.findBy(1)

        coVerify(exactly = 1) { rewardDao.findBy(1) }
    }

    @Test
    fun findAll() = runTest {
        target.findAll()

        coVerify(exactly = 1) { rewardDao.findAll() }
    }
}
