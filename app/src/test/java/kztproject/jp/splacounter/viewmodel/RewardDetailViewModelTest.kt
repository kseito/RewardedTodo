package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.database.RewardDao
import org.junit.After
import org.junit.Before
import org.junit.Test
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
        viewModel.setName("test")
        viewModel.setPoint("1")
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
        viewModel.setName("test")
        viewModel.saveReward()

        verify(mockCallback).onError(R.string.error_empty_point)
    }
}