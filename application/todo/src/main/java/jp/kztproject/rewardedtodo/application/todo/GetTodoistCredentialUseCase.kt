package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import kotlinx.coroutines.flow.Flow

interface GetTodoistCredentialUseCase {

    suspend fun execute(): TodoistCredential?

    fun executeAsFlow(): Flow<TodoistCredential?>
}
