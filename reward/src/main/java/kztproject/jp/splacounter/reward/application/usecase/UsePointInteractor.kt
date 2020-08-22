package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class UsePointInteractor @Inject constructor(
        private val pointRepository: IPointRepository
) : UsePointUseCase {
    override suspend fun execute(reward: Reward): RewardUser {
        return pointRepository.consumePoint(reward.consumePoint)
    }
}