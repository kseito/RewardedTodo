package jp.kztproject.rewardedtodo.application.reward.usecase

import javax.inject.Inject
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository

class DeleteRewardInteractor @Inject constructor(
    private val rewardRepository: IRewardRepository
) : DeleteRewardUseCase {
    override suspend fun execute(reward: Reward) {
        rewardRepository.delete(reward)
    }
}
