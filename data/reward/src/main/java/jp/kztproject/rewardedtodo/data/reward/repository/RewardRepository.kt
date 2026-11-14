package jp.kztproject.rewardedtodo.data.reward.repository

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.data.reward.network.*
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardRepository @Inject constructor(private val api: RewardApi, private val users: RewardUserIdRepository) :
    IRewardRepository {
    override suspend fun createOrUpdate(reward: RewardInput) {
        val probability = requireNotNull(reward.probability)
        require(probability.isFinite() && probability in 0f..100f)
        val bps = BigDecimal(probability.toString()).movePointRight(2).intValueExact()
        val request =
            SaveRewardRequest(requireNotNull(reward.name), reward.description, bps, reward.needRepeat, reward.version)
        rewardRequest {
            val user = users.getUserId()
            if (reward.id == null) api.create(user, request) else api.update(user, reward.id!!, request)
        }
    }
    override suspend fun delete(reward: Reward) {
        rewardRequest { api.delete(users.getUserId(), reward.rewardId.value, reward.version) }
    }
    override suspend fun findBy(id: Int): Reward? = findAll().find { it.rewardId.value == id }
    override suspend fun findAll(): List<Reward> = rewardRequest {
        api.list(users.getUserId()).map(RewardResponse::domain)
    }
    override suspend fun findAllAsFlow(): Flow<List<Reward>> = flow { emit(findAll()) }
}
