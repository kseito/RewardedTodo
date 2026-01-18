package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.LotteryBoxFactory
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.Ticket
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import javax.inject.Inject

class BatchLotteryInteractor @Inject constructor(private val ticketRepository: ITicketRepository) :
    BatchLotteryUseCase {
    override suspend fun execute(rewards: RewardCollection, count: Int): Result<BatchLotteryResult> {
        try {
            ticketRepository.consumeTickets(count)
        } catch (e: LackOfTicketsException) {
            return Result.failure(e)
        }

        val lotteryBox = LotteryBoxFactory.create(rewards)
        val wonRewards = mutableListOf<Reward>()
        var missCount = 0

        repeat(count) {
            val luckyNumber = (1..Ticket.ISSUE_LIMIT).random()
            val ticket = lotteryBox.draw(luckyNumber)
            if (ticket is Ticket.Prize) {
                val reward = rewards.findBy(ticket.rewardId)
                wonRewards.add(reward)
            } else {
                missCount++
            }
        }

        return Result.success(BatchLotteryResult(wonRewards, missCount))
    }
}
