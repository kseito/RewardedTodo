package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.IPointRepository
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardUser
import javax.inject.Inject

class UsePointInteractor @Inject constructor(
        private val pointRepository: IPointRepository
) : UsePointUseCase {
    override suspend fun execute(reward: Reward): RewardUser {
        return pointRepository.consumePoint(reward.consumePoint)
    }
}