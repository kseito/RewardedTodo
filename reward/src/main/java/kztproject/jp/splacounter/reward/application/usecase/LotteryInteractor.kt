package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import kztproject.jp.splacounter.reward.domain.model.LotteryBoxFactory
import kztproject.jp.splacounter.reward.domain.model.Ticket
import javax.inject.Inject
import kotlin.random.Random

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(rewardEntities: List<RewardEntity>): RewardEntity? {
        val lotteryBox = LotteryBoxFactory.create(rewardEntities)
        val luckyNumber = Random.nextInt(Ticket.ISSUE_LIMIT)
        val ticket = lotteryBox.draw(luckyNumber)
        if (ticket is Ticket.Prize) {
            return rewardEntities.first { it.id == ticket.rewardId }
        }
        return null
    }
}