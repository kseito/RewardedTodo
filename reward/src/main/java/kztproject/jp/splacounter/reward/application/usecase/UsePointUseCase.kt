package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser

interface UsePointUseCase {
    suspend fun execute(reward: Reward): RewardUser
}