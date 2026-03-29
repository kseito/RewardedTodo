package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.ConsumePointRequest
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
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
        val token = userIdRepository.getToken()
        val userId = userIdRepository.getUserId()
        val response = api.consumePoints(userId, "Bearer $token", ConsumePointRequest(count))
        if (response.code() == 401) {
            // キャッシュ済み userId のトークンハッシュが未登録の場合、再登録してリトライ
            userIdRepository.clearUserId()
            val newUserId = userIdRepository.getUserId()
            val retryResponse = api.consumePoints(newUserId, "Bearer $token", ConsumePointRequest(count))
            when {
                retryResponse.code() == 422 -> throw LackOfTicketsException()
                !retryResponse.isSuccessful -> throw IOException("Server error: ${retryResponse.code()}")
            }
            return
        }
        when {
            response.code() == 422 -> throw LackOfTicketsException()
            !response.isSuccessful -> throw IOException("Server error: ${response.code()}")
        }
    }

    override suspend fun getNumberOfTicket(): Flow<Int> = flow {
        val token = userIdRepository.getToken()
        val userId = userIdRepository.getUserId()
        try {
            val response = api.getPoints(userId, "Bearer $token")
            emit(response.availablePoints)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                userIdRepository.clearUserId()
                val newUserId = userIdRepository.getUserId()
                emit(api.getPoints(newUserId, "Bearer $token").availablePoints)
            } else {
                throw e
            }
        }
    }
}
