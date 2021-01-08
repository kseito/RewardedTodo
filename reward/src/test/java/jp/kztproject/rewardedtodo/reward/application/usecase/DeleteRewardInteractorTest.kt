package jp.kztproject.rewardedtodo.reward.application.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import jp.kztproject.rewardedtodo.application.reward.DeleteRewardInteractor
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