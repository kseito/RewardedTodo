package kztproject.jp.splacounter.reward.list.ui

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.usecase.LotteryUseCase
import kztproject.jp.splacounter.reward.presentation.list.RewardListViewModel
import kztproject.jp.splacounter.reward.presentation.list.RewardViewModelCallback
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import project.seito.screen_transition.preference.PrefsWrapper
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val mockPointRepository: IPointRepository = mock()

    private val mockDao: IRewardRepository = mock()

    private val prefsWrapper = PrefsWrapper(RuntimeEnvironment.application)

    private val useCase: LotteryUseCase = mock()

    private lateinit var viewModel: RewardListViewModel

    @Before
    fun setup() {
        viewModel = RewardListViewModel(mockPointRepository, mockDao, prefsWrapper, useCase)
        viewModel.setCallback(mockCallback)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testShowRewardAdd() {
        viewModel.showRewardDetail()

        verify(mockCallback, times(1)).showRewardDetail()
    }

    @Test
    fun testGetRewards() {
        runBlocking { whenever(mockDao.findAll()).thenReturn(arrayOf(DummyCreator.createDummyReward())) }
        viewModel.loadRewards()

        assertThat(viewModel.isEmpty.value).isFalse()
        verify(mockCallback, times(1)).showRewards(any())
    }

    @Test
    fun testGetRewards_Empty() {
        viewModel.loadRewards()

        assertThat(viewModel.isEmpty.value).isTrue()
    }

    @Test
    fun testAcquireRewardSuccess() {
        runBlocking { whenever(mockPointRepository.consumePoint(anyLong(), anyInt())).thenReturn(DummyCreator.createDummyRewardUser()) }
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.acquireReward()

        verify(mockCallback, times(1)).successAcquireReward(any(), anyInt())
    }

    @Test
    fun testAcquireRewardFailure_PointShortage() {
        runBlocking { whenever(mockPointRepository.consumePoint(anyLong(), anyInt())).thenReturn(DummyCreator.createDummyRewardUser()) }

        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(1)
        viewModel.selectedReward = reward
        viewModel.acquireReward()

        verify(mockCallback, times(1)).showError()
    }


    @Test
    fun testAcquireRewardFailure_SocketTimeOut() {
        runBlocking { whenever(mockPointRepository.consumePoint(anyLong(), anyInt())).thenAnswer { throw SocketTimeoutException() } }
        viewModel.setPoint(20)
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.acquireReward()
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testRemoveReward() {
        runBlocking {
            viewModel.deleteRewardIfNeeded(DummyCreator.createDummyReward())

            verify(mockDao, times(0)).delete(any())
            verify(mockCallback, times(0)).onRewardDeleted(any())
        }
    }

    @Test
    fun testNotRemoveReward() {
        runBlocking {
            viewModel.deleteRewardIfNeeded(DummyCreator.createDummyNoRepeatReward())

            verify(mockDao, times(1)).delete(any())
            verify(mockCallback, times(0)).onRewardDeleted(any())
        }
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
        runBlocking {
            val reward = DummyCreator.createDummyReward()
            viewModel.deleteReward(reward, true)

            verify(mockDao).delete(any())
            verify(mockCallback).onRewardDeleted(reward)
        }
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
        runBlocking { whenever(mockPointRepository.loadPoint(anyLong())).thenReturn(dummyPoint) }
        viewModel.loadPoint()

        assertThat(viewModel.rewardPoint.value).isEqualTo(10)
        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
    }

    @Test
    fun testLoadPoint_Failure() {
        runBlocking { whenever(mockPointRepository.loadPoint(anyLong())).thenAnswer { throw SocketTimeoutException() } }
        viewModel.loadPoint()

        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
        verify(mockCallback).onPointLoadFailed()
    }
}