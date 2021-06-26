package jp.kztproject.rewardedtodo.reward.application.usecase

import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import jp.kztproject.rewardedtodo.application.reward.usecase.UsePointInteractor
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