package jp.kztproject.rewardedtodo.application.reward.usecase

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardInteractor
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import org.junit.Test

class DeleteRewardInteractorTest {

    private val mockRewardDao: IRewardRepository = mock()
    private val interactor = DeleteRewardInteractor(mockRewardDao)

    @Test
    fun shouldDeleteReward() {
        runBlocking {
            val dummyReward = DummyCreator.createDummyReward()

            interactor.execute(dummyReward)

            verify(mockRewardDao, times(1)).delete(any())
        }
    }
}