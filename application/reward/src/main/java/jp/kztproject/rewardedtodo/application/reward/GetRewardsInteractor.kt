package jp.kztproject.rewardedtodo.application.reward

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.domain.reward.Reward
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