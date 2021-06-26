package jp.kztproject.rewardedtodo.application.reward.usecase

import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LotteryInteractorTest {

    private val mockTicketRepository: ITicketRepository = mock()

    private val interactor = LotteryInteractor(mockTicketRepository)

    @Test
    fun shouldGetPrize() {
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
            verify(mockTicketRepository, times(1)).consumeTicket()
        }
    }

    @Test
    fun shouldMissPrize() {
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