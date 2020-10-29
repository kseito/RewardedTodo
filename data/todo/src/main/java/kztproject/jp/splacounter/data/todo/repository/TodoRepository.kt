package kztproject.jp.splacounter.data.todo.repository

import kztproject.jp.splacounter.data.todo.TodoDao
import kztproject.jp.splacounter.data.todo.TodoEntity
import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) : ITodoRepository {

    override suspend fun findAll(): List<Todo> {
//        (1..10)
//                .map {
//                    Todo(it.toLong(), "Test Todo $it", 0.5f, true)
//                }.forEach { todoDao.insertOrUpdate(it.convert()) }

        return todoDao.findAll().map { it.convert() }
    }

    override suspend fun update(todo: Todo) {
        todoDao.insertOrUpdate(todo.convert())
    }

    private fun TodoEntity.convert(): Todo {
        return Todo(
                this.id,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }

    private fun Todo.convert(): TodoEntity {
        return TodoEntity(
                this.id,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }
}