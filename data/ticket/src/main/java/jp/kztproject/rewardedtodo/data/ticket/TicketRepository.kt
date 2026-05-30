package jp.kztproject.rewardedtodo.data.ticket

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * チケットリポジトリの [ITicketRepository] 実装。
 *
 * Todoist連携の有無で内部的に振る舞いを切り替える:
 * - Todoist APIトークン未保存（未連携）: [LocalTicketRepository] に委譲し、ローカル(DataStore)で
 *   チケットを管理する。
 * - Todoist APIトークン保存済み（連携済み）: [NetworkTicketRepository] に委譲し、サーバ経由で
 *   チケットを管理する（加算はTodoist Webhookが担当）。
 *
 * 判定は操作ごとに行う。`Flow` 取得時の判定は取得時点のスナップショットで、連携状態が
 * 途中で変わった場合は再呼び出し（画面再表示）が必要。
 */
class TicketRepository @Inject constructor(
    private val localRepository: LocalTicketRepository,
    private val networkRepository: NetworkTicketRepository,
    private val dataStore: DataStore<Preferences>,
) : ITicketRepository {

    override suspend fun addTicket(numberOfTicket: Int) {
        delegate().addTicket(numberOfTicket)
    }

    override suspend fun consumeTicket() {
        delegate().consumeTicket()
    }

    override suspend fun consumeTickets(count: Int) {
        delegate().consumeTickets(count)
    }

    override suspend fun getNumberOfTicket(): Flow<Int> = delegate().getNumberOfTicket()

    private suspend fun delegate(): ITicketRepository = if (isTodoistConnected()) networkRepository else localRepository

    private suspend fun isTodoistConnected(): Boolean = dataStore.data
        .map { it[UserPreferencesKeys.TODOIST_API_TOKEN].orEmpty() }
        .first()
        .isNotBlank()
}
