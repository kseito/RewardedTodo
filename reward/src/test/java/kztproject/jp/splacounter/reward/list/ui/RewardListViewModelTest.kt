package kztproject.jp.splacounter.reward.list.ui

import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.application.usecase.DeleteRewardUseCase
import kztproject.jp.splacounter.reward.application.usecase.GetRewardsUseCase
import kztproject.jp.splacounter.reward.application.usecase.LotteryUseCase
import kztproject.jp.splacounter.reward.presentation.list.RewardListViewModel
import kztproject.jp.splacounter.reward.presentation.list.RewardViewModelCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.robolectric.RobolectricTestRunner
import project.seito.screen_transition.preference.PrefsWrapper
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val mockPointRepository: IPointRepository = mock()

    private val prefsWrapper = PrefsWrapper(ApplicationProvider.getApplicationContext())

    private val mockLotteryUseCase: LotteryUseCase = mock()

    private lateinit var viewModel: RewardListViewModel

    private val mockGetRewardsUseCase: GetRewardsUseCase = mock()

    private val mockDeleteRewardUseCase: DeleteRewardUseCase = mock()

    @Before
    fun setup() {
        viewModel = RewardListViewModel(
                mockPointRepository,
                prefsWrapper,
                mockLotteryUseCase,
                mockGetRewardsUseCase,
                mockDeleteRewardUseCase)
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
        runBlocking { whenever(mockGetRewardsUseCase.execute()).thenReturn(listOf(DummyCreator.createDummyReward())) }
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
        viewModel.selectedRewardEntity = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.acquireReward()

        verify(mockCallback, times(1)).successAcquireReward(any(), anyInt())
    }

    @Test
    fun testAcquireRewardFailure_PointShortage() {
        runBlocking { whenever(mockPointRepository.consumePoint(anyLong(), anyInt())).thenReturn(DummyCreator.createDummyRewardUser()) }

        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(1)
        viewModel.selectedRewardEntity = reward
        viewModel.acquireReward()

        verify(mockCallback, times(1)).showError()
    }


    @Test
    fun testAcquireRewardFailure_SocketTimeOut() {
        runBlocking { whenever(mockPointRepository.consumePoint(anyLong(), anyInt())).thenAnswer { throw SocketTimeoutException() } }
        viewModel.setPoint(20)
        viewModel.selectedRewardEntity = DummyCreator.createDummyReward()
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
        viewModel.selectedRewardEntity = DummyCreator.createDummyReward()
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
        viewModel.selectedRewardEntity = reward
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