package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import javax.inject.Inject

class SaveApiTokenInteractor @Inject constructor(private val apiTokenRepository: IApiTokenRepository) :
    SaveApiTokenUseCase {

    override suspend fun execute(tokenValue: String): Result<Unit> = try {
        val apiToken = ApiToken.create(tokenValue)
        apiTokenRepository.saveToken(apiToken)
    } catch (e: IllegalArgumentException) {
        Result.failure(TokenError.AuthenticationFailed)
    } catch (e: Exception) {
        Result.failure(TokenError.NetworkError(e))
    }
}
