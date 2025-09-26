package jp.kztproject.rewardedtodo.application.todo

interface SaveApiTokenUseCase {

    suspend fun execute(tokenValue: String): Result<Unit>
}
