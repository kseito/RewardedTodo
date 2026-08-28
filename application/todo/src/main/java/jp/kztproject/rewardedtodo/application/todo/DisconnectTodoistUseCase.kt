package jp.kztproject.rewardedtodo.application.todo

interface DisconnectTodoistUseCase {

    /**
     * 端末に保存したクレデンシャルを削除して連携を解除する。
     *
     * Todoist側のアクセス許可は取り消されない。失効APIが `client_secret` を要求し、
     * 秘密鍵を持たない公開クライアントからは呼び出せないため、Todoist側の取り消しは
     * ユーザーがTodoistの設定画面から行う必要がある。
     */
    suspend fun execute(): Result<Unit>
}
