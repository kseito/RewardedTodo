package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession

interface ITodoistAuthSessionRepository {

    suspend fun saveSession(session: TodoistAuthSession)

    suspend fun getSession(): TodoistAuthSession?

    suspend fun clearSession()
}
