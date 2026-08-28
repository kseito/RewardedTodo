package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthSessionRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import javax.inject.Inject

class DisconnectTodoistInteractor @Inject constructor(
    private val authSessionRepository: ITodoistAuthSessionRepository,
    private val credentialRepository: ITodoistCredentialRepository,
) : DisconnectTodoistUseCase {

    override suspend fun execute(): Result<Unit> = runCatching {
        credentialRepository.deleteCredential()
        authSessionRepository.clearSession()
    }
}
