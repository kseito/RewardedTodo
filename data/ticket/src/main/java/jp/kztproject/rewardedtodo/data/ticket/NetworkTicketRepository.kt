package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.ConsumePointRequest
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class NetworkTicketRepository @Inject constructor(
    private val api: RewardServerApi,
    private val userIdRepository: RewardUserIdRepository,
) : ITicketRepository {

    override suspend fun addTicket(numberOfTicket: Int) {
        // サーバー側の Todoist Webhook がポイントを加算するため、アプリ側では何もしない
    }

    override suspend fun consumeTicket() {
        consumeTickets(1)
    }

    override suspend fun consumeTickets(count: Int) {
        withRetryOn401 { userId, token ->
            api.consumePoints(userId, "Bearer $token", ConsumePointRequest(count))
        }
    }

    override suspend fun getNumberOfTicket(): Flow<Int> = flow {
        val points = withRetryOn401 { userId, token ->
            api.getPoints(userId, "Bearer $token")
        }
        emit(points.availablePoints)
    }

    private suspend fun <T> withRetryOn401(block: suspend (userId: String, token: String) -> T): T {
        val token = userIdRepository.getToken()
        val userId = userIdRepository.getUserId()
        return try {
            block(userId, token)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                // キャッシュ済み userId のトークンハッシュが未登録の場合、再登録してリトライ
                userIdRepository.clearUserId()
                val newUserId = userIdRepository.getUserId()
                block(newUserId, token)
            } else if (e.code() == 422) {
                throw LackOfTicketsException()
            } else {
                throw e
            }
        }
    }
}
