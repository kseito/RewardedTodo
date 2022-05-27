package jp.kztproject.rewardedtodo.data.todo.repository

import android.content.SharedPreferences
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import jp.kztproject.rewardedtodo.data.todo.TodoEntity
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.todo.domain.Todo
import jp.kztproject.rewardedtodo.todo.domain.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class TodoRepository @Inject constructor(
        private val todoDao: TodoDao,
        private val todoistApi: TodoistApi,
        @Named("encrypted") private val preferences: SharedPreferences
) : ITodoRepository {

    override fun findAll(): Flow<List<Todo>> {
        return todoDao.findAllAsFlow().map { list ->
            list.map { todo -> todo.convert() }
        }
    }

    override suspend fun sync() {
        if (!preferences.getString(EncryptedStore.TODOIST_ACCESS_TOKEN, null).isNullOrEmpty()) {
            val latestTasks = todoistApi.fetchTasks("today|overdue")
            val localTasks = todoDao.findAll()

            val localTaskIds = localTasks.map { it.todoistId }
            latestTasks.forEach { task ->
                if (localTaskIds.contains(task.id)) {
                    //Update if both
                    val todoEntity = todoDao.findBy(task.id)
                    todoDao.insertOrUpdate(todoEntity.resetIsDone())
                } else {
                    //Insert if only in Todoist
                    todoDao.insertOrUpdate(task.convert(false))
                }
            }
            localTasks.filter { entity ->
                !latestTasks.map { it.id }.contains(entity.todoistId)
            }.forEach {
                if (it.isRepeat) {
                    todoDao.completeTaskById(it.id)
                } else {
                    todoDao.delete(it)
                }
            }
        }
    }

    override suspend fun update(todo: Todo) {
        todoDao.insertOrUpdate(todo.convert())
    }

    override suspend fun complete(todo: Todo) {
        if (!todo.isRepeat) {
            todoDao.delete(todo.convert())
        }
        todo.todoistId?.let {
            todoistApi.completeTask(it)
            todoDao.completeTaskByTodoistId(it)
        }
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
                this.isRepeat,
                false
        )
    }

    private fun Task.convert(isDone: Boolean): TodoEntity {
        return TodoEntity(
                0,
                this.id,
                this.content,
                0.5F,
                this.due.recurring,
                isDone
        )
    }

    private fun TodoEntity.resetIsDone(): TodoEntity {
        return copy(isDone = false)
    }
}
