package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken

interface RefreshTodoistTokenUseCase {

    /**
     * 保存済みのリフレッシュトークンでアクセストークンを再発行し、保存して返す。
     */
    suspend fun execute(): Result<ApiToken>
}
