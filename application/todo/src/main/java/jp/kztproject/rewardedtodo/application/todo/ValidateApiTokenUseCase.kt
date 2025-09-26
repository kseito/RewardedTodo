package jp.kztproject.rewardedtodo.application.todo

interface ValidateApiTokenUseCase {

    suspend fun execute(tokenValue: String): Result<Unit>
}