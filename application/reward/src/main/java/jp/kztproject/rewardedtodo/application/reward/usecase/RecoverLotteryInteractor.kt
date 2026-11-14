package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import javax.inject.Inject

class RecoverLotteryInteractor @Inject constructor(private val lottery: ILotteryRepository) : RecoverLotteryUseCase {
    override suspend fun execute(): LotteryOutcome? = lottery.recoverPending()
}
