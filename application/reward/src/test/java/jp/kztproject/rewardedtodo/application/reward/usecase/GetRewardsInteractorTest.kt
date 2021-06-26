package jp.kztproject.rewardedtodo.application.reward.usecase

import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsInteractor
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import org.junit.Test

class GetRewardsInteractorTest {

    private val mockRewardRepository: IRewardRepository = mock()

    @Test
    fun shouldGetRewards() {
        runBlocking {
            val dummyRewards = DummyCreator.createDummyRewards()
            whenever(mockRewardRepository.findAll()).thenReturn(dummyRewards)
            GetRewardsInteractor(mockRewardRepository).execute()
            verify(mockRewardRepository, times(1)).findAll()
        }
    }
}
