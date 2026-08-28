package jp.kztproject.rewardedtodo.domain.todo

/**
 * Todoist連携のクレデンシャル一式。
 *
 * @param accessToken APIリクエストに載せるアクセストークン
 * @param refreshToken 再発行用のトークン。リフレッシュ非対応のアプリ設定ではnullになる
 * @param expiresAt アクセストークンの有効期限(epoch millis)。無期限の場合はnull
 */
data class TodoistCredential(
    val accessToken: ApiToken,
    val refreshToken: RefreshToken? = null,
    val expiresAt: Long? = null,
) {

    /**
     * 有効期限が切れている（あるいは切れる直前）かどうか。
     *
     * 通信の往復中に期限を跨いで401になるのを避けるため、実際の期限より[EXPIRY_MARGIN_MILLIS]だけ
     * 手前で失効扱いにする。
     */
    fun isExpired(nowMillis: Long): Boolean {
        val expiry = expiresAt ?: return false
        return nowMillis >= expiry - EXPIRY_MARGIN_MILLIS
    }

    /**
     * リフレッシュ結果を反映した新しいクレデンシャルを返す。
     *
     * Todoistは消費済みリフレッシュトークンの60秒以内の再試行に対して`refresh_token`を返さない。
     * その場合に現在の値を失うと再連携が必要になるため、nullなら既存の値を引き継ぐ。
     */
    fun merge(refreshed: TodoistCredential): TodoistCredential = refreshed.copy(
        refreshToken = refreshed.refreshToken ?: refreshToken,
    )

    companion object {
        const val EXPIRY_MARGIN_MILLIS = 60_000L
    }
}
