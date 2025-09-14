package jp.kztproject.rewardedtodo.feature.reward

import io.mockk.coEvery
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockLotteryUseCase = mockk<LotteryUseCase>()

    private lateinit var viewModel: RewardListViewModel

    private val mockGetRewardsUseCase = mockk<GetRewardsUseCase>()

    private val mockGetPointUseCase = mockk<GetPointUseCase>()

    private val mockSaveRewardUseCase = mockk<SaveRewardUseCase>()

    private val mockDeleteRewardUseCase = mockk<DeleteRewardUseCase>()

    @Before
    fun setup() {
        coEvery { mockGetRewardsUseCase.executeAsFlow() } returns flowOf(
            listOf(
                DummyCreator.createDummyReward(),
            ),
        )
        val dummyPoint = DummyCreator.createDummyRewardPoint()
        coEvery { mockGetPointUseCase.execute() } returns flowOf(dummyPoint)

        viewModel = RewardListViewModel(
            mockLotteryUseCase,
            mockGetRewardsUseCase,
            mockGetPointUseCase,
            mockSaveRewardUseCase,
            mockDeleteRewardUseCase,
        )

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetRewards() = runTest {
        val rewardList = viewModel.rewardList.first { it.isNotEmpty() }
        assertThat(rewardList.size).isEqualTo(1)
    }

    @Test
    fun testLoadPoint() = runTest {
        viewModel.loadPoint()

        assertThat(viewModel.rewardPoint.value).isEqualTo(10)
    }
}
