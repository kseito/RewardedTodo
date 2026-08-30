package jp.kztproject.rewardedtodo.application.todo

interface CompleteTodoistAuthUseCase {

    /**
     * 認可画面から返されたリダイレクトURIを検証し、認可コードをアクセストークンに交換して保存する。
     *
     * @param redirectUri Auth Tabが返したリダイレクトURI（code / state / error を含む）
     */
    suspend fun execute(redirectUri: String): Result<Unit>
}
