package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import kztproject.jp.splacounter.DummyCreator
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class RewardViewModelTest {

    @Mock
    private val callback: RewardViewModel.Callback = mock()

    private lateinit var viewModel: RewardViewModel

    @Before
    fun setup() {
        viewModel = RewardViewModel()
        viewModel.setCallback(callback)
    }

    @Test
    fun testShowRewardAdd() {
        viewModel.showRewardAdd()

        verify(callback, times(1)).showRewardAdd()
    }

    @Test
    fun testGetRewards() {
        viewModel.getRewards()

        verify(callback, times(1)).showRewards(any())
    }

    @Test
    fun testCanConsumeSuccess() {
        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.canAcquireReward(reward)

        verify(callback, times(1)).showConfirmDialog(reward)
    }

    @Test
    fun testCanConsumeFailure() {
        viewModel.setPoint(0)
        viewModel.canAcquireReward(DummyCreator.createDummyReward())
        
        verify(callback, times(1)).showError()
    }
}