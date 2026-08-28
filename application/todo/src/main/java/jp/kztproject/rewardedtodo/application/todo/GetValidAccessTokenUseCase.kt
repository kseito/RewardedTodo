package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken

interface GetValidAccessTokenUseCase {

    /**
     * 有効なアクセストークンを返す。期限切れの場合はリフレッシュを試みる。
     *
     * 未連携、またはリフレッシュに失敗した場合はnullを返す。
     */
    suspend fun execute(): ApiToken?
}
