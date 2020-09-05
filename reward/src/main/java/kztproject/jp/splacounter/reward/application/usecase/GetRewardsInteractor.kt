package kztproject.jp.splacounter.reward.application.usecase

import kotlinx.coroutines.flow.Flow
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
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