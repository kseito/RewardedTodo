package kztproject.jp.splacounter.reward.detail.ui


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.application.usecase.DeleteRewardUseCase
import kztproject.jp.splacounter.reward.application.usecase.GetRewardUseCase
import kztproject.jp.splacounter.reward.application.usecase.SaveRewardUseCase
import kztproject.jp.splacounter.reward.domain.model.RewardId
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailViewModel
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailViewModelCallback
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import project.seito.reward.R

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
            whenever(mockGetRewardUseCase.execute(RewardId(anyInt()))).thenReturn(reward)
        }
        viewModel.initialize(RewardId(1))
        viewModel.saveReward()

        Mockito.verify(mockCallback).onSaveCompleted(anyString())
    }

    @Test
    fun testSaveRewardWithoutTitle() {
        viewModel.saveReward()

        Mockito.verify(mockCallback).onError(R.string.error_empty_title)
    }
}