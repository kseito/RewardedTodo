package kztproject.jp.splacounter.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.data.ticket.ITicketRepository
import kztproject.jp.splacounter.reward.domain.model.*
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