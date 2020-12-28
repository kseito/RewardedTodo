package jp.kztproject.rewardedtodo.reward.repository

import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import jp.kztproject.rewardedtodo.data.reward.api.DummyCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.data.reward.api.RewardPointService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.times
import org.robolectric.RobolectricTestRunner
import project.seito.screen_transition.preference.PrefsWrapper

@RunWith(RobolectricTestRunner::class)
class PointRepositoryTest {

    private val mockRewardPointService: RewardPointService = mock {
        onBlocking { getPoint(anyLong()) } doReturn GlobalScope.async { DummyCreator.createDummyRewardPoint() }
        onBlocking { updatePoint(anyLong(), anyInt()) } doReturn GlobalScope.async { DummyCreator.createDummyRewardUser() }
    }
    private val preferences: PrefsWrapper = PrefsWrapper(ApplicationProvider.getApplicationContext())

    private val target: PointRepository = PointRepository(mockRewardPointService, preferences)

    @Test
    fun loadPoint() {
        runBlocking {
            val actual = target.loadPoint()

            assertThat(actual.value).isEqualTo(10)
        }
    }

    @Suppress("DeferredResultUnused")
    @Test
    fun consumePoint() {
        preferences.userId = 1

        runBlocking { target.consumePoint(2) }

        verify(mockRewardPointService, times(1)).updatePoint(1, -2)
    }
}