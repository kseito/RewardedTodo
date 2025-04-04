package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteRewardInteractorTest {

    private val mockRewardDao: IRewardRepository = mockk(relaxed = true)
    private val interactor = DeleteRewardInteractor(mockRewardDao)

    @Test
    fun shouldDeleteReward() = runTest {
        val dummyReward = DummyCreator.createDummyReward()

        interactor.execute(dummyReward)

        coVerify(exactly = 1) { mockRewardDao.delete(any()) }
    }
}
