package kztproject.jp.splacounter.reward.list.ui

import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.usecase.*
import kztproject.jp.splacounter.reward.presentation.list.RewardListViewModel
import kztproject.jp.splacounter.reward.presentation.list.RewardViewModelCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.RobolectricTestRunner
import project.seito.screen_transition.preference.PrefsWrapper
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val prefsWrapper = PrefsWrapper(ApplicationProvider.getApplicationContext())

    private val mockLotteryUseCase: LotteryUseCase = mock()

    private lateinit var viewModel: RewardListViewModel

    private val mockGetRewardsUseCase: GetRewardsUseCase = mock()

    private val mockDeleteRewardUseCase: DeleteRewardUseCase = mock()

    private val mockGetPointUseCase: GetPointUseCase = mock()

    private val mockUsePointUseCase: UsePointUseCase = mock()

    @Before
    fun setup() {
        viewModel = RewardListViewModel(
                prefsWrapper,
                mockLotteryUseCase,
                mockGetRewardsUseCase,
                mockDeleteRewardUseCase,
                mockGetPointUseCase,
                mockUsePointUseCase
        )
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
        runBlocking { whenever(mockGetRewardsUseCase.executeAsFlow()).thenReturn(flowOf(listOf(DummyCreator.createDummyReward()))) }
        viewModel.loadRewards()

        assertThat(viewModel.isEmpty.value).isFalse()
        verify(mockCallback, times(1)).showRewards(any())
    }

    @Test
    fun testGetRewards_Empty() {
        runBlocking { whenever(mockGetRewardsUseCase.executeAsFlow()).thenReturn(flowOf(emptyList())) }
        viewModel.loadRewards()

        assertThat(viewModel.isEmpty.value).isTrue()
    }

    @Test
    fun testAcquireRewardSuccess() {
        runBlocking { whenever(mockUsePointUseCase.execute(any())).thenReturn(DummyCreator.createDummyRewardUser()) }
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.acquireReward()

        verify(mockCallback, times(1)).successAcquireReward(any(), anyInt())
    }

    @Test
    fun testAcquireRewardFailure_PointShortage() {
        runBlocking { whenever(mockUsePointUseCase.execute(any())).thenReturn(DummyCreator.createDummyRewardUser()) }

        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(1)
        viewModel.selectedReward = reward
        viewModel.acquireReward()

        verify(mockCallback, times(1)).showError()
    }


    @Test
    fun testAcquireRewardFailure_SocketTimeOut() {
        runBlocking { whenever(mockUsePointUseCase.execute(any())).thenAnswer { throw SocketTimeoutException() } }
        viewModel.setPoint(20)
        viewModel.selectedReward = DummyCreator.createDummyReward()
        viewModel.acquireReward()
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testRemoveReward() {
        runBlocking {
            viewModel.deleteRewardIfNeeded(DummyCreator.createDummyReward())

            verify(mockDeleteRewardUseCase, times(0)).execute(any())
            verify(mockCallback, times(0)).onRewardDeleted(any())
        }
    }

    @Test
    fun testNotRemoveReward() {
        runBlocking {
            viewModel.deleteRewardIfNeeded(DummyCreator.createDummyNoRepeatReward())

            verify(mockDeleteRewardUseCase, times(1)).execute(any())
            verify(mockCallback, times(0)).onRewardDeleted(any())
        }
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

            verify(mockDeleteRewardUseCase).execute(any())
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
        runBlocking { whenever(mockGetPointUseCase.execute()).thenReturn(dummyPoint) }
        viewModel.loadPoint()

        assertThat(viewModel.rewardPoint.value).isEqualTo(10)
        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
    }

    @Test
    fun testLoadPoint_Failure() {
        runBlocking { whenever(mockGetPointUseCase.execute()).thenAnswer { throw SocketTimeoutException() } }
        viewModel.loadPoint()

        verify(mockCallback).onStartLoadingPoint()
        verify(mockCallback).onTerminateLoadingPoint()
        verify(mockCallback).onPointLoadFailed()
    }
}