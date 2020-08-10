package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.domain.model.Reward
import javax.inject.Inject

class GetRewardsInteractor @Inject constructor(
        private val rewardDao: IRewardRepository
) : GetRewardsUseCase {

    override suspend fun execute(): List<Reward> {
        return rewardDao.findAll()
                .map { it.convert() }
    }
}