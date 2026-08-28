package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import javax.inject.Inject

class GetValidAccessTokenInteractor @Inject constructor(
    private val credentialRepository: ITodoistCredentialRepository,
    private val refreshTodoistTokenUseCase: RefreshTodoistTokenUseCase,
    private val currentTimeProvider: CurrentTimeProvider,
) : GetValidAccessTokenUseCase {

    override suspend fun execute(): ApiToken? {
        val credential = credentialRepository.getCredential() ?: return null
        if (!credential.isExpired(currentTimeProvider.nowMillis())) {
            return credential.accessToken
        }
        return refreshTodoistTokenUseCase.execute().getOrNull()
    }
}
