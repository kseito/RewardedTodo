package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.preference.PrefsWrapper
import kztproject.jp.splacounter.reward.database.RewardDao
import kztproject.jp.splacounter.reward.list.ui.RewardViewModel
import kztproject.jp.splacounter.reward.list.ui.RewardViewModelCallback
import kztproject.jp.splacounter.reward.repository.IPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
class RewardViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val mockPointRepository: IPointRepository = mock()

    private val mockDao: RewardDao = mock()

    private val prefsWrapper = PrefsWrapper(RuntimeEnvironment.application)

    private lateinit var viewModel: RewardViewModel

    @Before
    fun setup() {
        viewModel = RewardViewModel(mockPointRepository, mockDao, prefsWrapper)
        viewModel.setCallback(mockCallback)

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
        viewModel.showRewardDetail()

        verify(mockCallback, times(1)).showRewardDetail()
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
    fun testAcquireRewardSuccess() {
        whenever(mockPointRepository.consumePoint(anyLong(), anyInt()))
                .thenReturn(Single.just(DummyCreator.createDummyRewardUser()))
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.acquireReward()

        verify(mockCallback, times(1)).successAcquireReward(any(), anyInt())
    }

    @Test
    fun testAcquireRewardFailure_PointShortage() {
        whenever(mockPointRepository.consumePoint(anyLong(), anyInt()))
                .thenReturn(Single.just(DummyCreator.createDummyRewardUser()))
        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(1)
        viewModel.selectedReward = reward
        viewModel.acquireReward()

        verify(mockCallback, times(1)).showError()
    }


    @Test
    fun testAcquireRewardFailure_SocketTimeOut() {
        whenever(mockPointRepository.consumePoint(anyLong(), anyInt()))
                .thenReturn(Single.error(SocketTimeoutException()))
        viewModel.setPoint(20)
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.acquireReward()
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testRemoveReward() {
        viewModel.deleteRewardIfNeeded(DummyCreator.createDummyReward())

        verify(mockDao, times(0)).deleteReward(any())
        verify(mockCallback, times(0)).onRewardDeleted(any())
    }

    @Test
    fun testNotRemoveReward() {
        viewModel.deleteRewardIfNeeded(DummyCreator.createDummyNoRepeatReward())

        verify(mockDao, times(1)).deleteReward(any())
        verify(mockCallback, times(0)).onRewardDeleted(any())
    }

    @Test
    fun testSelectReward() {
        val reward = DummyCreator.createDummyReward()
        viewModel.rewardList.add(reward)
        viewModel.switchReward(reward)

        verify(mockCallback).onRewardSelected(anyInt())
    }

    @Test
    fun testDeselectReward() {
        val reward = DummyCreator.createDummyReward()
        viewModel.rewardList.add(reward)
        viewModel.switchReward(reward)
        viewModel.switchReward(reward)

        verify(mockCallback).onRewardSelected(anyInt())
        verify(mockCallback).onRewardDeSelected(anyInt())
    }

    @Test
    fun testReselectReward() {
        val rewards = listOf(DummyCreator.createDummyReward(),
                DummyCreator.createDummyNoRepeatReward())
        viewModel.rewardList.addAll(rewards)
        viewModel.switchReward(rewards[0])
        viewModel.switchReward(rewards[1])

        verify(mockCallback, times(2)).onRewardSelected(anyInt())
        verify(mockCallback, times(1)).onRewardDeSelected(anyInt())
    }

    @Test
    fun testConfirmDelete() {
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.confirmDelete()

        verify(mockCallback).showDeleteConfirmDialog(any())
    }

    @Test
    fun testDeleteReward() {
        val reward = DummyCreator.createDummyReward()
        viewModel.deleteReward(reward, true)

        verify(mockDao).deleteReward(any())
        verify(mockCallback).onRewardDeleted(reward)
    }

    @Test
    fun testEditReward() {
        val reward = DummyCreator.createDummyReward()
        viewModel.selectedReward = reward
        viewModel.editReward()

        verify(mockCallback).onRewardEditSelected(reward)
    }

    @Test
    fun testLoadPoint_Success() {
        val dummyPoint = DummyCreator.createDummyRewardPoint()
        whenever(mockPointRepository.loadPoint(anyLong())).thenReturn(Single.just(dummyPoint))
        viewModel.loadPoint()

        assertThat(viewModel.point.get()).isEqualTo(10)
        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
    }

    @Test
    fun testLoadPoint_Failure() {
        whenever(mockPointRepository.loadPoint(anyLong())).thenReturn(Single.error(SocketTimeoutException()))
        viewModel.loadPoint()

        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
        verify(mockCallback).onPointLoadFailed()
    }
}