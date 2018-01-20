package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
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
}