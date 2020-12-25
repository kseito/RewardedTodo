package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.repository.IRewardRepository
import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import jp.kztproject.rewardedtodo.reward.domain.model.RewardId
import javax.inject.Inject

class GetRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : GetRewardUseCase {
    override suspend fun execute(id: RewardId): Reward? {
        return rewardRepository.findBy(id.value)
    }
}