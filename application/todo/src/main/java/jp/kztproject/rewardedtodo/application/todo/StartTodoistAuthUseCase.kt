package jp.kztproject.rewardedtodo.application.todo

interface StartTodoistAuthUseCase {

    /**
     * PKCEのcode_verifierとstateを発行して保存し、認可画面のURLを組み立てて返す。
     */
    suspend fun execute(): Result<String>
}
