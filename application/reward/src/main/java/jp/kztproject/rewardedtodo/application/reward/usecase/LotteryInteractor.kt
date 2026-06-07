package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.ITicketRepository
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
            // 繰り返し取得不可の報酬は当選で消費されるため削除する。
            // 削除失敗時はResult.failureにしてUI側で扱えるようにする（executeはResult契約のため）。
            if (!reward.needRepeat) {
                try {
                    rewardRepository.delete(reward)
                } catch (e: Exception) {
                    return Result.failure(e)
                }
            }
            return Result.success(reward)
        }
        return Result.success(null)
    }
}
