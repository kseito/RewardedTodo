package jp.kztproject.rewardedtodo.data.reward.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.data.reward.network.LotteryRequest
import jp.kztproject.rewardedtodo.data.reward.network.RewardApi
import jp.kztproject.rewardedtodo.domain.reward.LotteryOutcome
import jp.kztproject.rewardedtodo.domain.reward.exception.PendingLotteryException
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotteryRepository @Inject constructor(
    private val api: RewardApi,
    private val users: RewardUserIdRepository,
    private val dataStore: DataStore<Preferences>,
) : ILotteryRepository {
    private val mutex = Mutex()
    private fun key(user: String) = stringPreferencesKey("pending_lottery_$user")

    override suspend fun draw(count: Int): LotteryOutcome = mutex.withLock {
        require(count in 1..10)
        val user = users.getUserId()
        if (dataStore.data.first()[key(user)] != null) throw PendingLotteryException()
        val request = LotteryRequest(UUID.randomUUID().toString(), count)
        dataStore.edit { it[key(user)] = "${request.requestId}|${request.count}" }
        send(user, request)
    }

    override suspend fun recoverPending(): LotteryOutcome? = mutex.withLock {
        val user = users.getUserId()
        val storedRequest = dataStore.data.first()[key(user)] ?: return@withLock null
        val parts = storedRequest.split('|')
        send(user, LotteryRequest(parts[0], parts[1].toInt()))
    }

    private suspend fun send(user: String, request: LotteryRequest): LotteryOutcome = rewardRequest {
        try {
            val result = api.draw(user, request).domain()
            dataStore.edit { it.remove(key(user)) }
            result
        } catch (e: HttpException) {
            // These responses guarantee no execution. Keep the key for timeouts, 5xx,
            // authentication errors and request conflicts whose outcome is still unknown.
            if (e.code() == 400 || e.code() == 422) dataStore.edit { it.remove(key(user)) }
            throw e
        }
    }
}
