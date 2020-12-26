package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import javax.inject.Inject

class GetRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : GetRewardUseCase {
    override suspend fun execute(id: RewardId): Reward? {
        return rewardRepository.findBy(id.value)
    }
}