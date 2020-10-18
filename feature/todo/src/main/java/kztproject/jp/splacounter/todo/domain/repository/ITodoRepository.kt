package kztproject.jp.splacounter.todo.domain.repository

import kztproject.jp.splacounter.todo.domain.Todo

interface ITodoRepository {
    fun findAll(): List<Todo>
}