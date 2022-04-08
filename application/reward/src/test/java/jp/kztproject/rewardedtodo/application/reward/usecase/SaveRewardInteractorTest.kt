package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.application.reward.model.error.RewardProbabilityEmptyException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardTitleEmptyException
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.kotlin.mock

class SaveRewardInteractorTest {

    private val mockIRewardRepository: IRewardRepository = mock()
    private val interactor = SaveRewardInteractor(mockIRewardRepository)
    private val filledReward = RewardInput(
        null,
        "test_name",
        3f,
        "test_description",
        false
    )

    @Test
    fun shouldSuccess_WhenSaveFilledReward() = runBlockingTest {
        val actual = interactor.execute(filledReward)
        assertThat(actual).isEqualTo(Result.success(Unit))
    }

    @Test
    fun shouldFail_WhenSaveRewardWithEmptyTitle() = runBlockingTest {
        val rewardInput = filledReward.copy(name = null)

        val actual = interactor.execute(rewardInput)
        assertThat(actual.exceptionOrNull()).isInstanceOf(RewardTitleEmptyException().javaClass)
    }

    @Test
    fun shouldFail_WhenSaveRewardWithEmptyProbability() = runBlockingTest {
        val rewardInput = filledReward.copy(probability = null)

        val actual = interactor.execute(rewardInput)
        assertThat(actual.exceptionOrNull()).isInstanceOf(RewardProbabilityEmptyException().javaClass)
    }
}
