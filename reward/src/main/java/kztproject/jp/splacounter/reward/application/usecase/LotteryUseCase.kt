package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.database.model.Reward

interface LotteryUseCase {
    fun execute(rewards: List<Reward>): Int
}