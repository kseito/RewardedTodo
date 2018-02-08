package kztproject.jp.splacounter.api

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import kztproject.jp.splacounter.DummyCreator
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class MiniatureGardenClientTest {

    private val mockService: MiniatureGardenService = Mockito.mock(MiniatureGardenService::class.java)

    private lateinit var client: MiniatureGardenClient

    @Before
    fun setup() {
        client = MiniatureGardenClient(mockService)
    }

    @Test
    fun getCounterSuccess() {
        val counter = DummyCreator.createDummyCounter()
        whenever(mockService.getCounter(ArgumentMatchers.anyInt())).thenReturn(Observable.just(counter))

        client.getCounter(1)
                .test()
                .assertNoErrors()
                .assertResult(counter)
                .assertComplete()
    }

    @Test
    fun getCounterFailed() {
        val exception = IllegalArgumentException()

        whenever(mockService.getCounter(ArgumentMatchers.anyInt())).thenReturn(Observable.error(exception))

        client.getCounter(1)
                .test()
                .assertError(exception)
    }

}
