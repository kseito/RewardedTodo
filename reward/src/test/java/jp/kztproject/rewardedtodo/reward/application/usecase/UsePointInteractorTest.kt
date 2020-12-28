package jp.kztproject.rewardedtodo.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import jp.kztproject.rewardedtodo.domain.reward.repository.IPointRepository
import org.junit.Test

class UsePointInteractorTest {

    private val mockPointRepository: IPointRepository = mock()

    @Test
    fun shouldUsePoint() {
        runBlocking {
            val dummyReward = DummyCreator.createDummyReward()

            UsePointInteractor(mockPointRepository).execute(dummyReward)

            verify(mockPointRepository, times(1)).consumePoint(dummyReward.consumePoint)
        }

    }
}