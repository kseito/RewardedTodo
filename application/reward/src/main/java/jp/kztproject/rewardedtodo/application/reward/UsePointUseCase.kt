package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardUser

interface UsePointUseCase {
    suspend fun execute(reward: Reward): RewardUser
}