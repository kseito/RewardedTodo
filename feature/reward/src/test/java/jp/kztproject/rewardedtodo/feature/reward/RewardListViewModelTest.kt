package jp.kztproject.rewardedtodo.feature.reward

import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            whenever(mockGetRewardsUseCase.executeAsFlow()).thenReturn(
                flowOf(
                    listOf(
                        DummyCreator.createDummyReward()
                    )
                )
            )
            val dummyPoint = DummyCreator.createDummyRewardPoint()
            whenever(mockGetPointUseCase.execute()).thenReturn(flowOf( dummyPoint))

            viewModel = RewardListViewModel(
                mockLotteryUseCase,
                mockGetRewardsUseCase,
                mockGetPointUseCase,
                mockSaveRewardUseCase,
                mockDeleteRewardUseCase
            )
        }

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetRewards() = runTest {
        viewModel.loadRewards()

        assertThat(viewModel.rewardList.value!!.size).isEqualTo(1)
    }

    @Test
    fun testLoadPoint() = runTest {
        viewModel.loadPoint()

        assertThat(viewModel.rewardPoint.value).isEqualTo(10)
    }
}
