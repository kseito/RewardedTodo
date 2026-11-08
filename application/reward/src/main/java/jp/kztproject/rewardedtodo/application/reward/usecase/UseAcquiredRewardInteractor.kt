package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.IAcquiredRewardRepository
import javax.inject.Inject

class UseAcquiredRewardInteractor @Inject constructor(private val inventory: IAcquiredRewardRepository) :
    UseAcquiredRewardUseCase {
    override suspend fun execute(id: AcquiredReward.Id): AcquiredReward = inventory.use(id)
}
