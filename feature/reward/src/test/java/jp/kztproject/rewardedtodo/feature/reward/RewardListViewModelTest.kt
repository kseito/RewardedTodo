package jp.kztproject.rewardedtodo.feature.reward

import jp.kztproject.rewardedtodo.application.reward.usecase.*
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockLotteryUseCase: LotteryUseCase = mock()

    private lateinit var viewModel: RewardListViewModel

    private val mockGetRewardsUseCase: GetRewardsUseCase = mock()

    private val mockGetPointUseCase: GetPointUseCase = mock()

    private val mockSaveRewardUseCase: SaveRewardUseCase = mock()

    private val mockDeleteRewardUseCase: DeleteRewardUseCase = mock()

    @Before
    fun setup() {
        viewModel = RewardListViewModel(
            mockLotteryUseCase,
            mockGetRewardsUseCase,
            mockGetPointUseCase,
            mockSaveRewardUseCase,
            mockDeleteRewardUseCase
        )

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetRewards() = runTest {
        whenever(mockGetRewardsUseCase.executeAsFlow()).thenReturn(
            flowOf(
                listOf(
                    DummyCreator.createDummyReward()
                )
            )
        )
        viewModel.loadRewards()

        assertThat(viewModel.rewardList.value!!.size).isEqualTo(1)
    }

    @Test
    fun testLoadPoint() = runTest {
        val dummyPoint = DummyCreator.createDummyRewardPoint()
        whenever(mockGetPointUseCase.execute()).thenReturn(flowOf( dummyPoint))
        viewModel.loadPoint()

        assertThat(viewModel.rewardPoint.value).isEqualTo(10)
    }
}
