package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward

interface GetRewardsUseCase {
    suspend fun execute(): List<Reward>
}