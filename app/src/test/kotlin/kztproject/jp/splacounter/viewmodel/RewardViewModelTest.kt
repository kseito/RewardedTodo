package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.MiniatureGardenClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock

class RewardViewModelTest {

    @Mock
    private val mockCallback: RewardViewModelCallback = mock()

    @Mock
    private val mockMiniatureGardenClient: MiniatureGardenClient = mock()

    private lateinit var viewModel: RewardViewModel

    @Before
    fun setup() {
        viewModel = RewardViewModel(mockMiniatureGardenClient)
        viewModel.setCallback(mockCallback)

        val scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testShowRewardAdd() {
        viewModel.showRewardAdd()

        verify(mockCallback, times(1)).showRewardAdd()
    }

    @Test
    fun testGetRewards() {
        viewModel.getRewards()

        verify(mockCallback, times(1)).showRewards(any())
    }

    @Test
    fun testCanConsumeSuccess() {
        val reward = DummyCreator.createDummyReward()
        viewModel.setPoint(20)
        viewModel.canAcquireReward(reward)

        verify(mockCallback, times(1)).showConfirmDialog(reward)
    }

    @Test
    fun testCanAcquireReward() {
        viewModel.setPoint(0)
        viewModel.canAcquireReward(DummyCreator.createDummyReward())
        
        verify(mockCallback, times(1)).showError()
    }

    @Test
    fun testAcquireRewardSuccess() {
        whenever(mockMiniatureGardenClient.consumeCounter(anyInt())).thenReturn(Observable.just(DummyCreator.createDummyCounter()))
        viewModel.acquireReward(reward)

        verify(mockCallback, times(1)).successAcquireReward(anyInt())
    }

//    @Test
//    fun testAcquireRewardFailure() {
//        //TODO
//    }
}