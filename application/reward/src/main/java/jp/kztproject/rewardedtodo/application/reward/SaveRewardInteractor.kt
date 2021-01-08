package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.application.reward.model.Error
import jp.kztproject.rewardedtodo.application.reward.model.Failure
import jp.kztproject.rewardedtodo.application.reward.model.Result
import jp.kztproject.rewardedtodo.application.reward.model.Success
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import javax.inject.Inject

class SaveRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : SaveRewardUseCase {
    override suspend fun execute(reward: RewardInput): Result<Unit> {
        return when {
            reward.name.isNullOrEmpty() -> Failure(Error.EmptyTitle)
            reward.consumePoint == null -> Failure(Error.EmptyPoint)
            reward.probability == null -> Failure(Error.EmptyProbability)
            else -> {
                rewardRepository.createOrUpdate(reward)
                Success(Unit)
            }
        }
    }
}