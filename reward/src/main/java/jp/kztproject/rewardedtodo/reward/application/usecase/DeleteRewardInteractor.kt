package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.repository.IRewardRepository
import jp.kztproject.rewardedtodo.domain.reward.Reward
import javax.inject.Inject

class DeleteRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : DeleteRewardUseCase {
    override suspend fun execute(reward: Reward) {
        rewardRepository.delete(reward)
    }
}