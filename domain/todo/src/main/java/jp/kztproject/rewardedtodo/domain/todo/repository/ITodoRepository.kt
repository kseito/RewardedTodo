package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun findAll(): Flow<List<Todo>>

    suspend fun sync()

    suspend fun update(todo: Todo)

    suspend fun complete(todo: Todo)

    suspend fun delete(todo: Todo)
}
