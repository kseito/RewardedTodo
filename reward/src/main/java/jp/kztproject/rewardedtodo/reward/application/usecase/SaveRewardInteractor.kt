package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.reward.application.model.Error
import jp.kztproject.rewardedtodo.reward.application.model.Failure
import jp.kztproject.rewardedtodo.reward.application.model.Result
import jp.kztproject.rewardedtodo.reward.application.model.Success
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