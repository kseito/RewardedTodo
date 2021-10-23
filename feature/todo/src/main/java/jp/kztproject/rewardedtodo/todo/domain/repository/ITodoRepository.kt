package jp.kztproject.rewardedtodo.todo.domain.repository

import jp.kztproject.rewardedtodo.todo.domain.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun findAll(): Flow<List<Todo>>

    suspend fun sync()

    suspend fun update(todo: Todo)

    suspend fun complete(todo: Todo)

    suspend fun delete(todo: Todo)
}