package kztproject.jp.splacounter.reward.detail.ui


import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Matchers
import org.mockito.Mockito
import project.seito.reward.R

class RewardDetailViewModelTest {

    private val mockCallback: RewardDetailViewModelCallback = mock()

    private val mockRewardRepository: IRewardRepository = mock()

    private val viewModel: RewardDetailViewModel = RewardDetailViewModel(mockRewardRepository)

    @Before
    fun setup() {
        viewModel.setCallback(mockCallback)

        val scheduler: Scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testSaveReward() {
        viewModel.reward.name = "test"
        viewModel.reward.consumePoint = 1
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
        viewModel.reward.name = "test"
        viewModel.saveReward()

        Mockito.verify(mockCallback).onError(R.string.error_empty_point)
    }

    @Test
    fun testInitialize() {
        val reward = DummyCreator.createDummyReward()
        whenever(mockRewardRepository.findBy(ArgumentMatchers.anyInt())).thenReturn(reward)
        viewModel.initialize(1)

        Assertions.assertThat(viewModel.reward).isEqualTo(reward)
    }
}