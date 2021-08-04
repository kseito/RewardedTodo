package jp.kztproject.rewardedtodo.data.todo.repository

import android.content.SharedPreferences
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import jp.kztproject.rewardedtodo.data.todo.TodoEntity
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.data.todoist.model.Tasks
import jp.kztproject.rewardedtodo.todo.domain.Todo
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

class TodoRepository @Inject constructor(
        private val todoDao: TodoDao,
        private val todoistApi: TodoistApi,
        @Named("encrypted") private val preferences: SharedPreferences
) : ITodoRepository {

    override fun findAll(): Flow<List<Todo>> {
        return if (preferences.getString(EncryptedStore.TODOIST_ACCESS_TOKEN, null).isNullOrEmpty()) {
            todoDao.findAll().map { list ->
                list.map { todo -> todo.convert() }
            }
        } else {
            // TODO need error handling
            flow { emit(todoistApi.fetchTasks("today|overdue")) }
                    .combine(todoDao.findAll()) { a: Tasks, b: List<TodoEntity> ->
                        // TODO need existing task case
                        // support only new task case
                        a.tasks.filter { task ->
                            !b.map { it.todoistId }.contains(task.id)
                        }.forEach {
                            todoDao.insertOrUpdate(it.convert())
                        }
                    }.flatMapLatest {
                        todoDao.findAll().map { list ->
                            list.map { todo -> todo.convert() }
                        }
                    }
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
                this.todoistId,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }

    private fun Todo.convert(): TodoEntity {
        return TodoEntity(
                this.id ?: 0,
                this.todoistId,
                this.name,
                this.numberOfTicketsObtained,
                this.isRepeat
        )
    }

    private fun Task.convert(): TodoEntity {
        return TodoEntity(
                0,
                this.id,
                this.content,
                0.5F,
                this.due.recurring
        )
    }
}