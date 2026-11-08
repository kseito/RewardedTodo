package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.LotteryOutcome

interface RecoverLotteryUseCase {
    suspend fun execute(): LotteryOutcome?
}
