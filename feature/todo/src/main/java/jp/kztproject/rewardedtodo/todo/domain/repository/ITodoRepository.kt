package jp.kztproject.rewardedtodo.todo.domain.repository

import kotlinx.coroutines.flow.Flow
import jp.kztproject.rewardedtodo.todo.domain.Todo

interface ITodoRepository {
    fun findAll(): Flow<List<Todo>>

    suspend fun update(todo: Todo)

    suspend fun delete(todo: Todo)
}