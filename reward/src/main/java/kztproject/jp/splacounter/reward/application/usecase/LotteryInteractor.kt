package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.LotteryBoxFactory
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardCollection
import kztproject.jp.splacounter.reward.domain.model.Ticket
import javax.inject.Inject
import kotlin.random.Random

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(rewards: RewardCollection): Reward? {
        val lotteryBox = LotteryBoxFactory.create(rewards)
        val luckyNumber = Random.nextInt(Ticket.ISSUE_LIMIT)
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            return rewards.findBy(ticket.rewardId)
        }
        return null
    }
}