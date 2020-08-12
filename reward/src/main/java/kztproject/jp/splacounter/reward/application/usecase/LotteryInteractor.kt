package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.LotteryBoxFactory
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.Ticket
import javax.inject.Inject
import kotlin.random.Random

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(reward: List<Reward>): Reward? {
        val lotteryBox = LotteryBoxFactory.create(reward)
        val luckyNumber = Random.nextInt(Ticket.ISSUE_LIMIT)
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            return reward.first { it.rewardId.value == ticket.rewardId }
        }
        return null
    }
}