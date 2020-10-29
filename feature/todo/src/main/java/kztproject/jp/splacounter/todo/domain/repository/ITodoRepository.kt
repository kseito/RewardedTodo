package kztproject.jp.splacounter.todo.domain.repository

import kztproject.jp.splacounter.todo.domain.Todo

interface ITodoRepository {
    suspend fun findAll(): List<Todo>

    suspend fun update(todo: Todo)
}