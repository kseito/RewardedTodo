package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.CommonException
import jp.kztproject.rewardedtodo.reward.application.model.Failure
import jp.kztproject.rewardedtodo.reward.application.model.Result
import jp.kztproject.rewardedtodo.reward.application.model.Success
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import project.seito.reward.R
import javax.inject.Inject

class SaveRewardInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : SaveRewardUseCase {
    override suspend fun execute(reward: RewardInput): Result<Unit> {
        return when {
            reward.name.isNullOrEmpty() -> Failure(CommonException(R.string.error_empty_title))
            reward.consumePoint == null -> Failure(CommonException(R.string.error_empty_point))
            reward.probability == null -> Failure(CommonException(R.string.error_empty_probability))
            else -> {
                rewardRepository.createOrUpdate(reward)
                Success(Unit)
            }
        }
    }
}