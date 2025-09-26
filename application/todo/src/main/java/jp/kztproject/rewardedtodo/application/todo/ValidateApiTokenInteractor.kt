package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

class ValidateApiTokenInteractor @Inject constructor() : ValidateApiTokenUseCase {

    override suspend fun execute(tokenValue: String): Result<Unit> = try {
        withTimeout(TIMEOUT_MILLIS) {
            // Try to create ApiToken - this validates the format
            ApiToken.create(tokenValue)
            Result.success(Unit)
        }
    } catch (e: Exception) {
        when (e) {
            is TimeoutException,
            is TimeoutCancellationException,
            ->
                Result.failure(TokenError.Timeout)
            else -> Result.failure(TokenError.NetworkError(e))
        }
    }

    private companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
