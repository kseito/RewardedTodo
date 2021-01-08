package jp.kztproject.rewardedtodo.reward.list.ui

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import jp.kztproject.rewardedtodo.application.reward.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.LotteryUseCase
import jp.kztproject.rewardedtodo.reward.presentation.list.RewardListViewModel
import jp.kztproject.rewardedtodo.reward.presentation.list.RewardViewModelCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockCallback: RewardViewModelCallback = mock()

    private val mockLotteryUseCase: LotteryUseCase = mock()

    private lateinit var viewModel: RewardListViewModel

    private val mockGetRewardsUseCase: GetRewardsUseCase = mock()

    private val mockGetPointUseCase: GetPointUseCase = mock()

    @Before
    fun setup() {
        viewModel = RewardListViewModel(
                mockLotteryUseCase,
                mockGetRewardsUseCase,
                mockGetPointUseCase
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