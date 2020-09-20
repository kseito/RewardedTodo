package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardId
import javax.inject.Inject

class GetRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : GetRewardUseCase {
    override suspend fun execute(id: RewardId): Reward? {
        return rewardRepository.findBy(id.value)
    }
}