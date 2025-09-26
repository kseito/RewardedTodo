package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import javax.inject.Inject

class DeleteApiTokenInteractor @Inject constructor(
    private val apiTokenRepository: IApiTokenRepository
) : DeleteApiTokenUseCase {

    override suspend fun execute() {
        apiTokenRepository.deleteToken()
    }
}