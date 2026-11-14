package jp.kztproject.rewardedtodo.domain.reward.repository

import jp.kztproject.rewardedtodo.domain.reward.LotteryOutcome

interface ILotteryRepository {
    suspend fun draw(count: Int): LotteryOutcome
    suspend fun recoverPending(): LotteryOutcome?
}
