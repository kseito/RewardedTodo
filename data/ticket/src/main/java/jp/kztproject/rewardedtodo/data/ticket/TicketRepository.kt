package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.ConsumePointRequest
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.repository.ITicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

/**
 * チケット（ポイント）の [ITicketRepository] 実装。
 *
 * 残数の管理はRewardサーバーが行う。加算はサーバー側のTodoist Webhookが担当するため、
 * アプリからは消費と取得だけを行う。
 */
class TicketRepository @Inject internal constructor(
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
        return try {
            try {
                block(userIdRepository.getUserId(), token)
            } catch (e: HttpException) {
                if (e.code() != 401) throw e
                // キャッシュ済み userId のトークンハッシュが未登録の場合、再登録して1度だけ再送する
                userIdRepository.clearUserId()
                block(userIdRepository.getUserId(), token)
            }
        } catch (e: HttpException) {
            // 再送の結果も同じ変換に通す。ここを通さないと残数不足が呼び出し元で判別できない
            if (e.code() == 422) throw LackOfTicketsException() else throw e
        }
    }
}
