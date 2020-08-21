package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import javax.inject.Inject

class DeleteRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : DeleteRewardUseCase {
    override suspend fun execute(reward: Reward) {
        rewardRepository.delete(reward)
    }
}