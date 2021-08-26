package jp.kztproject.rewardedtodo.todo.domain.repository

import jp.kztproject.rewardedtodo.todo.domain.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun findAll(): Flow<List<Todo>>

    fun sync(): Flow<List<Todo>>

    suspend fun update(todo: Todo)

    suspend fun delete(todo: Todo)
}