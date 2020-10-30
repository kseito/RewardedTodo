package kztproject.jp.splacounter.todo.domain.repository

import kotlinx.coroutines.flow.Flow
import kztproject.jp.splacounter.todo.domain.Todo

interface ITodoRepository {
    fun findAll(): Flow<List<Todo>>

    suspend fun update(todo: Todo)
}