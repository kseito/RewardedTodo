package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.mock
import kztproject.jp.splacounter.R
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class RewardAddViewModelTest{

    private val mockCallback: RewardAddViewModelCallback = mock()

    private val viewModel: RewardAddViewModel = RewardAddViewModel()

    @Before
    fun setup() {
        viewModel.setCallback(mockCallback)
    }

    @Test
    fun testSaveReward() {
        viewModel.setName("test")
        viewModel.setPoint("1")
        viewModel.saveReward()

        verify(mockCallback).onSaveCompleted()
    }

    @Test
    fun testSaveRewardWithoutTitle() {
        viewModel.saveReward()

        verify(mockCallback).onError(R.string.error_empty_title)
    }

    @Test
    fun testSaveRewardWithoutPoint() {
        viewModel.setName("test")
        viewModel.saveReward()

        verify(mockCallback).onError(R.string.error_empty_point)
    }
}