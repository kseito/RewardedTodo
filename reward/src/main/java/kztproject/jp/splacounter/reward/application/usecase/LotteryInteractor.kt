package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.database.model.Reward

class LotteryInteractor : LotteryUseCase {
    override fun execute(rewards: List<Reward>): Int {
        return 0
    }
}