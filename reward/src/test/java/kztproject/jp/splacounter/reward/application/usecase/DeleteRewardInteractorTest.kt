package kztproject.jp.splacounter.reward.application.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
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