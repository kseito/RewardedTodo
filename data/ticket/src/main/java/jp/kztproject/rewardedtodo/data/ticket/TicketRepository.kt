package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardServerApi
import jp.kztproject.rewardedtodo.domain.reward.repository.ITicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * チケット（ポイント）の [ITicketRepository] 実装。
 *
 * 残数の管理はRewardサーバーが行う。加算はサーバー側のTodoist Webhookが担当するため、
 * このリポジトリは残数を取得する。
 */
class TicketRepository @Inject internal constructor(
    private val api: RewardServerApi,
    private val userIdRepository: RewardUserIdRepository,
) : ITicketRepository {

    override suspend fun addTicket(numberOfTicket: Int) {
        // サーバー側の Todoist Webhook がポイントを加算するため、アプリ側では何もしない
    }

    override suspend fun getNumberOfTicket(): Flow<Int> = flow {
        val points = api.getPoints(userIdRepository.getUserId())
        emit(points.availablePoints)
    }
}
