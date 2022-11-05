package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteRewardInteractorTest {

    private val mockRewardDao: IRewardRepository = mock()
    private val interactor = DeleteRewardInteractor(mockRewardDao)

    @Test
    fun shouldDeleteReward() = runTest {
        val dummyReward = DummyCreator.createDummyReward()

        interactor.execute(dummyReward)

        verify(mockRewardDao, times(1)).delete(any())
    }
}
