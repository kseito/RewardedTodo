package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetRewardsInteractorTest {

    private val mockRewardRepository: IRewardRepository = mockk()

    @Test
    fun shouldGetRewards() = runTest {
        val dummyRewards = DummyCreator.createDummyRewards()
        coEvery { mockRewardRepository.findAll() } returns dummyRewards

        GetRewardsInteractor(mockRewardRepository).execute()

        coVerify(exactly = 1) { mockRewardRepository.findAll() }
    }
}
