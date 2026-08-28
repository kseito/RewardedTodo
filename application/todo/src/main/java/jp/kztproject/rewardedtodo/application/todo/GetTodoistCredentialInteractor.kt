package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodoistCredentialInteractor @Inject constructor(
    private val credentialRepository: ITodoistCredentialRepository,
) : GetTodoistCredentialUseCase {

    override suspend fun execute(): TodoistCredential? = credentialRepository.getCredential()

    override fun executeAsFlow(): Flow<TodoistCredential?> = credentialRepository.getCredentialAsFlow()
}
