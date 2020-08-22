package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val pointRepository: IPointRepository
) : GetPointUseCase {
    override suspend fun execute(): RewardPoint {
        return pointRepository.loadPoint()
    }

}