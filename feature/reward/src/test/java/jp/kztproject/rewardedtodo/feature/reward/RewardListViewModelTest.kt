package jp.kztproject.rewardedtodo.feature.reward

import io.mockk.coEvery
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class RewardListViewModelTest {

    private val mockLotteryUseCase = mockk<LotteryUseCase>()

    private val mockBatchLotteryUseCase = mockk<BatchLotteryUseCase>()

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
            mockBatchLotteryUseCase,
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
        rewardList.size shouldBe 1
    }

    @Test
    fun testRewardPoint() = runTest {
        val point = viewModel.rewardPoint.first { it != 0 }

        point shouldBe 10
    }

    @Test
    fun `rewardPoint refreshes after single lottery`() = runTest {
        // ネットワークモードのワンショットFlowを模し、execute()呼び出しごとに異なる値を返す
        coEvery { mockGetPointUseCase.execute() } returnsMany listOf(
            flowOf(NumberOfTicket(10)),
            flowOf(NumberOfTicket(9)),
        )
        coEvery { mockLotteryUseCase.execute(any()) } returns Result.success(null)
        viewModel = RewardListViewModel(
            mockLotteryUseCase,
            mockBatchLotteryUseCase,
            mockGetRewardsUseCase,
            mockGetPointUseCase,
            mockSaveRewardUseCase,
            mockDeleteRewardUseCase,
        )
        val collector = backgroundScope.launch { viewModel.rewardPoint.collect {} }
        viewModel.rewardPoint.first { it == 10 } shouldBe 10

        viewModel.startLottery()

        viewModel.rewardPoint.first { it == 9 } shouldBe 9
        collector.cancel()
    }
}
