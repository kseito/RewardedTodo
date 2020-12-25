package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.repository.IPointRepository
import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import jp.kztproject.rewardedtodo.reward.infrastructure.api.model.RewardUser
import javax.inject.Inject

class UsePointInteractor @Inject constructor(
        private val pointRepository: IPointRepository
) : UsePointUseCase {
    override suspend fun execute(reward: Reward): RewardUser {
        return pointRepository.consumePoint(reward.consumePoint)
    }
}