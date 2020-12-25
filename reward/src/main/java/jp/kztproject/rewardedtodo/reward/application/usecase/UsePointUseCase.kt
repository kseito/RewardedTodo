package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.reward.infrastructure.api.model.RewardUser

interface UsePointUseCase {
    suspend fun execute(reward: Reward): RewardUser
}