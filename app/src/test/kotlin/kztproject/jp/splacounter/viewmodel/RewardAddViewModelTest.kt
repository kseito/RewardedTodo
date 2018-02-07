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
    /**
     * 保存を押した時に、保存完了のコールバックを返す
     * 名前が空白の状態で保存を押した時に、エラーを表示するコールバックを返す
     * ポイントが空白の状態で保存を押した時に、エラーを表示するコールバックを返す
     */

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