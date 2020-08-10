package kztproject.jp.splacounter.reward.detail.ui


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailViewModel
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailViewModelCallback
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Matchers
import org.mockito.Mockito
import project.seito.reward.R

class RewardDetailViewModelTest {

    private val mockCallback: RewardDetailViewModelCallback = mock()

    private val mockRewardRepository: IRewardRepository = mock()

    private lateinit var viewModel: RewardDetailViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = RewardDetailViewModel(mockRewardRepository)
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
            val reward = Reward("test", 1, 10F, "test description", false)
            whenever(mockRewardRepository.findBy(anyInt())).thenReturn(reward)
        }
        viewModel.initialize(1)
        viewModel.saveReward()

        Mockito.verify(mockCallback).onSaveCompleted(Matchers.anyString())
    }

    @Test
    fun testSaveRewardWithoutTitle() {
        viewModel.saveReward()

        Mockito.verify(mockCallback).onError(R.string.error_empty_title)
    }

    @Test
    fun testSaveRewardWithoutPoint() {
        runBlocking {
            val reward = Reward("test", 0, 10F, "test description", false)
            whenever(mockRewardRepository.findBy(anyInt())).thenReturn(reward)
        }

        viewModel.initialize(1)
        viewModel.saveReward()

        Mockito.verify(mockCallback).onError(R.string.error_empty_point)
    }
}