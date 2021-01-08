package jp.kztproject.rewardedtodo.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
