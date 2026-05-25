package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.LotteryBoxFactory
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.Ticket
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import javax.inject.Inject

class LotteryInteractor @Inject constructor(
    private val ticketRepository: ITicketRepository,
    private val rewardRepository: IRewardRepository,
) : LotteryUseCase {
    override suspend fun execute(rewards: RewardCollection): Result<Reward?> {
        try {
            ticketRepository.consumeTicket()
        } catch (e: LackOfTicketsException) {
            return Result.failure(e)
        }

        val lotteryBox = LotteryBoxFactory.create(rewards)
        val luckyNumber = (0 until Ticket.ISSUE_LIMIT).random()
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            val reward = rewards.findBy(ticket.rewardId)
            // 繰り返し取得不可の報酬は当選で消費されるため削除する
            if (!reward.needRepeat) {
                rewardRepository.delete(reward)
            }
            return Result.success(reward)
        }
        return Result.success(null)
    }
}
