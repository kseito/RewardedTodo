package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.database.RewardDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Matchers.anyString
import org.mockito.Mockito.verify

class RewardDetailViewModelTest{

    private val mockCallback: RewardDetailViewModelCallback = mock()

    private val mockDao: RewardDao = mock()

    private val viewModel: RewardDetailViewModel = RewardDetailViewModel(mockDao)

    @Before
    fun setup() {
        viewModel.setCallback(mockCallback)

        val scheduler: Scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testSaveReward() {
        viewModel.reward.name = "test"
        viewModel.reward.consumePoint = 1
        viewModel.saveReward()

        verify(mockCallback).onSaveCompleted(anyString())
    }

    @Test
    fun testSaveRewardWithoutTitle() {
        viewModel.saveReward()

        verify(mockCallback).onError(R.string.error_empty_title)
    }

    @Test
    fun testSaveRewardWithoutPoint() {
        viewModel.reward.name = "test"
        viewModel.saveReward()

        verify(mockCallback).onError(R.string.error_empty_point)
    }

    @Test
    fun testInitialize() {
        val reward = DummyCreator.createDummyReward()
        whenever(mockDao.findBy(anyInt())).thenReturn(reward)
        viewModel.initialize(1)

        assertThat(viewModel.reward).isEqualTo(reward)
    }
}