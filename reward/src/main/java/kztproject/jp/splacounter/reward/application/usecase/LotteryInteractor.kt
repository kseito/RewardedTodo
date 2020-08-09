package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.domain.model.LotteryBoxFactory
import kztproject.jp.splacounter.reward.domain.model.Ticket
import javax.inject.Inject
import kotlin.random.Random

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(rewards: List<Reward>): Reward? {
        val lotteryBox = LotteryBoxFactory.create(rewards)
        val luckyNumber = Random.nextInt(Ticket.ISSUE_LIMIT)
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            return rewards.first { it.id == ticket.rewardId }
        }
        return null
    }
}