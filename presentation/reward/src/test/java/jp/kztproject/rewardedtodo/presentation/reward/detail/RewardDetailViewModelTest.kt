package jp.kztproject.rewardedtodo.presentation.reward.detail


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.application.reward.model.Success
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.presentation.reward.R
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class RewardDetailViewModelTest {

    private val mockCallback: RewardDetailViewModelCallback = mock()

    private val mockDeleteRewardUseCase: DeleteRewardUseCase = mock()
    private val mockGetRewardUseCase: GetRewardUseCase = mock()
    private val mockSaveRewardUseCase: SaveRewardUseCase = mock()

    private lateinit var viewModel: RewardDetailViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = RewardDetailViewModel(
                mockDeleteRewardUseCase,
                mockGetRewardUseCase,
                mockSaveRewardUseCase
        )
        viewModel.setCallback(mockCallback)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSaveReward() {
        runBlocking {
            val reward = DummyCreator.createDummyReward()
            val rewardInput = DummyCreator.createDummyRewardInput()
            whenever(mockGetRewardUseCase.execute(RewardId(anyInt()))).thenReturn(reward)
            whenever(mockSaveRewardUseCase.execute(rewardInput)).thenReturn(Success(Unit))
            viewModel.initialize(RewardId(1))

            viewModel.saveReward()

            Mockito.verify(mockCallback).onSaveCompleted(anyString())
        }
    }

    @Test
    fun testSaveRewardWithoutTitle() {
        viewModel.saveReward()

        Mockito.verify(mockCallback).onError(R.string.error_empty_title)
    }
}