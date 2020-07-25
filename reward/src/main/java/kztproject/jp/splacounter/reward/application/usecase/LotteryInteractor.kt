package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.database.model.Reward
import javax.inject.Inject

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(rewards: List<Reward>): Int? {
        return 0
    }
}