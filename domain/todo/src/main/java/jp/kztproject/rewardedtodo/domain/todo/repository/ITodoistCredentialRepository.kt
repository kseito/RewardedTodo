package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import kotlinx.coroutines.flow.Flow

interface ITodoistCredentialRepository {

    suspend fun saveCredential(credential: TodoistCredential)

    suspend fun getCredential(): TodoistCredential?

    fun getCredentialAsFlow(): Flow<TodoistCredential?>

    suspend fun deleteCredential()
}
