package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.IAcquiredRewardRepository
import javax.inject.Inject

class GetAcquiredRewardsInteractor @Inject constructor(private val inventory: IAcquiredRewardRepository) :
    GetAcquiredRewardsUseCase {
    override suspend fun execute(used: Boolean, cursor: String?): AcquiredRewardPage = inventory.findPage(used, cursor)
}
