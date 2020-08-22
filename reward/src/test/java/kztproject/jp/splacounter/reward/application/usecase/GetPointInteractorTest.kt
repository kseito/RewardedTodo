package kztproject.jp.splacounter.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import org.junit.Test

class GetPointInteractorTest {

    private val mockPointRepository: IPointRepository = mock()

    @Test
    fun shouldGetPoint() {
        runBlocking {
            val interactor = GetPointInteractor(mockPointRepository)
            interactor.execute()
            verify(mockPointRepository, times(1)).loadPoint()
        }
    }
}