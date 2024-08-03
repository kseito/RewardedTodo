package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.application.reward.model.error.OverMaxRewardsException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardProbabilityEmptyException
import jp.kztproject.rewardedtodo.application.reward.model.error.RewardTitleEmptyException
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import javax.inject.Inject

class SaveRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : SaveRewardUseCase {
    override suspend fun execute(reward: RewardInput): Result<Unit> {
        val rewardList = rewardRepository.findAll()
        if (rewardList.size >= RewardCollection.MAX) {
            return Result.failure(OverMaxRewardsException())
        }
        return when {
            reward.name.isNullOrEmpty() -> Result.failure(RewardTitleEmptyException())
            reward.probability == null -> Result.failure(RewardProbabilityEmptyException())
            else -> {
                rewardRepository.createOrUpdate(reward)
                Result.success(Unit)
            }
        }
    }
}
