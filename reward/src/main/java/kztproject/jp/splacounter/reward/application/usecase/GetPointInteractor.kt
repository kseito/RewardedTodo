package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val preferences: PrefsWrapper,
        private val pointRepository: IPointRepository
) : GetPointUseCase {
    override suspend fun execute(): RewardPoint {
        return pointRepository.loadPoint(preferences.userId)
    }

}