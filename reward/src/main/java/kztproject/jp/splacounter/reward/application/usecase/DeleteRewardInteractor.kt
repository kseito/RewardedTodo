package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import javax.inject.Inject

class DeleteRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : DeleteRewardUseCase {
    override suspend fun execute(reward: Reward) {
        val rewardModel = RewardEntity.from(reward)
        rewardRepository.delete(rewardModel)
    }
}