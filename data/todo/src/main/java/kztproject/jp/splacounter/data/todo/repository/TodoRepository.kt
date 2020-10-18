package kztproject.jp.splacounter.data.todo.repository

import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class TodoRepository @Inject constructor() : ITodoRepository {

    override fun findAll(): List<Todo> {
        return listOf(1, 2, 3, 4, 5)
                .map {
                    Todo(it.toLong(), "Test Todo $it", 0.5f, true)
                }
    }
}