package jp.kztproject.rewardedtodo.application.todo

interface DisconnectTodoistUseCase {

    /**
     * Todoist側でアクセストークンを失効させ、端末に保存したクレデンシャルを削除する。
     */
    suspend fun execute(): Result<Unit>
}
