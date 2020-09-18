package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.CommonException
import kztproject.jp.splacounter.reward.application.model.Failure
import kztproject.jp.splacounter.reward.application.model.Result
import kztproject.jp.splacounter.reward.application.model.Success
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.RewardInput
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