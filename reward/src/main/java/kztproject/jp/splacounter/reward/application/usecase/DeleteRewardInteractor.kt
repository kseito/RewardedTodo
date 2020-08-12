package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
import javax.inject.Inject

class DeleteRewardInteractor @Inject constructor(
        private val rewardDao: RewardDao
) : DeleteRewardUseCase {
    override suspend fun execute(reward: Reward) {
        val rewardModel = kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity.from(reward)
        rewardDao.deleteReward(rewardModel)
    }
}