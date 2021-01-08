package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.LotteryBoxFactory
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.Ticket
import javax.inject.Inject
import kotlin.random.Random

class LotteryInteractor @Inject constructor(
        private val ticketRepository: ITicketRepository
) : LotteryUseCase {
    override suspend fun execute(rewards: RewardCollection): Reward? {

        ticketRepository.consumeTicket()

        val lotteryBox = LotteryBoxFactory.create(rewards)
        val luckyNumber = Random.nextInt(Ticket.ISSUE_LIMIT)
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            return rewards.findBy(ticket.rewardId)
        }
        return null
    }
}