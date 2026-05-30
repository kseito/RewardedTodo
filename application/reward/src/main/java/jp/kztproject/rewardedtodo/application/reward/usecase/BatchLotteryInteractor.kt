package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.LotteryBoxFactory
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.Ticket
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import javax.inject.Inject

class BatchLotteryInteractor @Inject constructor(
    private val ticketRepository: ITicketRepository,
    private val rewardRepository: IRewardRepository,
) : BatchLotteryUseCase {
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
            val luckyNumber = (0 until Ticket.ISSUE_LIMIT).random()
            val ticket = lotteryBox.draw(luckyNumber)
            if (ticket is Ticket.Prize) {
                val reward = rewards.findBy(ticket.rewardId)
                wonRewards.add(reward)
            } else {
                missCount++
            }
        }

        // 繰り返し取得不可の当選報酬は消費されるため削除する（同一報酬の複数当選は1回だけ削除）。
        // 削除失敗時はResult.failureにしてUI側で扱えるようにする（executeはResult契約のため）。
        try {
            wonRewards.filterNot { it.needRepeat }
                .distinctBy { it.rewardId }
                .forEach { rewardRepository.delete(it) }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(BatchLotteryResult(wonRewards, missCount))
    }
}
