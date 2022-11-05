package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetRewardsInteractorTest {

    private val mockRewardRepository: IRewardRepository = mock()

    @Test
    fun shouldGetRewards() = runTest {
        val dummyRewards = DummyCreator.createDummyRewards()
        whenever(mockRewardRepository.findAll()).thenReturn(dummyRewards)
        GetRewardsInteractor(mockRewardRepository).execute()
        verify(mockRewardRepository, times(1)).findAll()
    }
}
