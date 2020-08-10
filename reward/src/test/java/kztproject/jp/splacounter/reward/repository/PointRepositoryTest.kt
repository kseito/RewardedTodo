package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.infrastructure.api.RewardPointService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.times

class PointRepositoryTest {

    private val mockRewardPointService: RewardPointService = mock {
        onBlocking { getPoint(anyLong()) } doReturn GlobalScope.async { DummyCreator.createDummyRewardPoint() }
        onBlocking { updatePoint(anyLong(), anyInt()) } doReturn GlobalScope.async { DummyCreator.createDummyRewardUser()}
    }

    private val target: PointRepository = PointRepository(mockRewardPointService)

    @Test
    fun loadPoint() {
        runBlocking {
            val actual = target.loadPoint(1)

            assertThat(actual.value).isEqualTo(10)
        }
    }

    @Suppress("DeferredResultUnused")
    @Test
    fun consumePoint() {
        runBlocking { target.consumePoint(1, 2) }

        verify(mockRewardPointService, times(1)).updatePoint(1, -2)
    }
}