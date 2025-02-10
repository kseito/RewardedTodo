package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import jp.kztproject.rewardedtodo.application.reward.model.error.OverMaxRewardsException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardProbabilityEmptyException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardTitleEmptyException
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveRewardInteractorTest {

    @MockK(relaxed = true)
    private lateinit var mockIRewardRepository: IRewardRepository
    private lateinit var interactor: SaveRewardInteractor
    private val filledReward = RewardInput(
        null,
        "test_name",
        3f,
        "test_description",
        false
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        interactor = SaveRewardInteractor(mockIRewardRepository)
    }

    @Test
    fun shouldSuccess_WhenSaveFilledReward() = runTest {
        val actual = interactor.execute(filledReward)
        assertThat(actual).isEqualTo(Result.success(Unit))
    }

    @Test
    fun shouldFail_WhenSaveRewardWithEmptyTitle() = runTest {
        val rewardInput = filledReward.copy(name = null)

        val actual = interactor.execute(rewardInput)
        assertThat(actual.exceptionOrNull()).isInstanceOf(RewardTitleEmptyException().javaClass)
    }

    @Test
    fun shouldFail_WhenSaveRewardWithEmptyProbability() = runTest {
        val rewardInput = filledReward.copy(probability = null)

        val actual = interactor.execute(rewardInput)
        assertThat(actual.exceptionOrNull()).isInstanceOf(RewardProbabilityEmptyException().javaClass)
    }
}
