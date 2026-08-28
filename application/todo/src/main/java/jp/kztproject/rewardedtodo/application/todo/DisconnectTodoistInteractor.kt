package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthSessionRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import javax.inject.Inject

class DisconnectTodoistInteractor @Inject constructor(
    private val authRepository: ITodoistAuthRepository,
    private val authSessionRepository: ITodoistAuthSessionRepository,
    private val credentialRepository: ITodoistCredentialRepository,
) : DisconnectTodoistUseCase {

    override suspend fun execute(): Result<Unit> = runCatching {
        // revokeの失敗（オフライン等）で端末側の解除まで巻き込まれると、ユーザーが
        // 連携を切れなくなるため、結果に関わらずローカルの削除は必ず行う
        credentialRepository.getCredential()?.let { credential ->
            authRepository.revoke(credential.accessToken)
        }
        credentialRepository.deleteCredential()
        authSessionRepository.clearSession()
    }
}
