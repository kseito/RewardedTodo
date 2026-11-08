package jp.kztproject.rewardedtodo.data.reward.repository

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.data.reward.network.RewardApi
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.IAcquiredRewardRepository
import javax.inject.Inject

class AcquiredRewardRepository @Inject constructor(
    private val api: RewardApi,
    private val users: RewardUserIdRepository,
) : IAcquiredRewardRepository {
    override suspend fun findPage(used: Boolean, cursor: String?): AcquiredRewardPage = rewardRequest {
        val page = api.inventory(users.getUserId(), if (used) "used" else "unused", cursor)
        AcquiredRewardPage(page.items.map { it.domain() }, page.nextCursor)
    }
    override suspend fun use(id: AcquiredReward.Id): AcquiredReward = rewardRequest {
        api.use(users.getUserId(), id.value).domain()
    }
}
