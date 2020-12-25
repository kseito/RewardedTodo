package jp.kztproject.rewardedtodo.reward.application.usecase

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.reward.application.repository.IRewardRepository
import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import javax.inject.Inject

class GetRewardsInteractor @Inject constructor(
        private val rewardRepository: IRewardRepository
) : GetRewardsUseCase {

    override suspend fun execute(): List<Reward> {
        return rewardRepository.findAll()
    }

    override suspend fun executeAsFlow(): Flow<List<Reward>> {
        return rewardRepository.findAllAsFlow()
    }
}