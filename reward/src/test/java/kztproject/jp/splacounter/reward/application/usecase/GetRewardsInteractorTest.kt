package kztproject.jp.splacounter.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
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
