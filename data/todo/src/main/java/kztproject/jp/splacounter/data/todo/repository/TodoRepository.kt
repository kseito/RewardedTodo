package kztproject.jp.splacounter.data.todo.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kztproject.jp.splacounter.data.todo.TodoDao
import kztproject.jp.splacounter.data.todo.TodoEntity
import kztproject.jp.splacounter.todo.domain.Todo
import kztproject.jp.splacounter.todo.domain.repository.ITodoRepository
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) : ITodoRepository {

    override fun findAll(): Flow<List<Todo>> {
//        (1..10)
//                .map {
//                    Todo(it.toLong(), "Test Todo $it", 0.5f, true)
//                }.forEach { todoDao.insertOrUpdate(it.convert()) }

        return todoDao.findAll().map { list ->
            list.map { todo -> todo.convert() }
        }
    }

    override suspend fun update(todo: Todo) {
        todoDao.insertOrUpdate(todo.convert())
    }

    override suspend fun delete(todo: Todo) {
        todoDao.delete(todo.convert())
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
                this.id ?: 0,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }
}