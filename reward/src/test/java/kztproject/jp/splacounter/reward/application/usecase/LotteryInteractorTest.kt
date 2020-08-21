package kztproject.jp.splacounter.reward.application.usecase

import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.reward.domain.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LotteryInteractorTest {

    private val interactor = LotteryInteractor()

    @Test
    fun gotPrize() {
        val rewards = RewardCollection(
                listOf(
                        Reward(RewardId(1),
                                RewardName("reward1"),
                                1,
                                Probability(100F),
                                RewardDescription(null),
                                true
                        )
                )
        )
        runBlocking {
            val response = interactor.execute(rewards)!!
            assertThat(response.rewardId).isEqualTo(RewardId(1))
        }
    }

    @Test
    fun missedPrize() {
        val rewards = RewardCollection(
                listOf(
                        Reward(RewardId(1),
                                RewardName("reward1"),
                                1,
                                Probability(0F),
                                RewardDescription(null),
                                true
                        )
                )
        )
        runBlocking {
            val response = interactor.execute(rewards)
            assertThat(response).isNull()
        }
    }
}