package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.MiniatureGardenClient
import kztproject.jp.splacounter.database.RewardDao
import kztproject.jp.splacounter.preference.PrefsWrapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
class RewardViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val mockMiniatureGardenClient: MiniatureGardenClient = mock()

    private val mockDao: RewardDao = mock()

    private lateinit var viewModel: RewardViewModel

    @Before
    fun setup() {
        viewModel = RewardViewModel(mockMiniatureGardenClient, mockDao)
        viewModel.setCallback(mockCallback)

        PrefsWrapper.initialize(RuntimeEnvironment.application)

        val scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testShowRewardAdd() {
        viewModel.showRewardAdd()

        verify(mockCallback, times(1)).showRewardAdd()
    }

    @Test
    fun testGetRewards() {
        whenever(mockDao.findAll()).thenReturn(arrayOf(DummyCreator.createDummyReward()))
        viewModel.getRewards()

        assertThat(viewModel.isEmpty.get()).isFalse()
        verify(mockCallback, times(1)).showRewards(any())
    }

    @Test
    fun testGetRewards_Empty() {
        viewModel.getRewards()

        assertThat(viewModel.isEmpty.get()).isTrue()
    }

    @Test
    fun testCanConsumeSuccess() {
        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.canAcquireReward(reward)

        verify(mockCallback, times(1)).showConfirmDialog(reward)
    }

    @Test
    fun testCanAcquireReward() {
        viewModel.setPoint(0)
        viewModel.canAcquireReward(DummyCreator.createDummyReward())
        
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testAcquireRewardSuccess() {
        whenever(mockMiniatureGardenClient.consumeCounter(anyInt(), anyInt())).thenReturn(Single.just(DummyCreator.createDummyCounter()))
        viewModel.acquireReward(DummyCreator.createDummyReward())

        verify(mockCallback, times(1)).successAcquireReward(any(), anyInt())
    }

    @Test
    fun testAcquireRewardFailure() {
        whenever(mockMiniatureGardenClient.consumeCounter(anyInt(), anyInt())).thenReturn(Single.error(SocketTimeoutException()))

        viewModel.acquireReward(DummyCreator.createDummyReward())
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testRemoveReward() {
        viewModel.removeRewardIfNeeded(DummyCreator.createDummyReward())

        verify(mockDao, times(0)).deleteReward(any())
    }

    @Test
    fun testNotRemoveReward() {
        viewModel.removeRewardIfNeeded(DummyCreator.createDummyNoRepeatReward())

        verify(mockDao, times(1)).deleteReward(any())
    }

    @Test
    fun testSelectReward() {
        val reward = DummyCreator.createDummyReward()
        viewModel.rewardList.add(reward)
        viewModel.selectReward(reward)

        verify(mockCallback).onRewardSelected()
    }
}