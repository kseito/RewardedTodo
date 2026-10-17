package jp.kztproject.rewardedtodo.data.todo.repository

import jp.kztproject.rewardedtodo.data.todo.LegacyTodoMigration
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoistApi: TodoistApi,
    private val settings: ITaskRewardRepository,
    private val migration: LegacyTodoMigration,
) : ITodoRepository {
    private val todos = MutableStateFlow<List<Todo>>(emptyList())
    private val mutex = Mutex()

    override fun findAll(): Flow<List<Todo>> = todos.asStateFlow()

    override suspend fun sync() = mutex.withLock {
        migration.migrate()
        val tasks = mutableListOf<Task>()
        var cursor: String? = null
        val seen = mutableSetOf<String>()
        do {
            val page = todoistApi.fetchTasks("today|overdue", cursor)
            tasks.addAll(page.results.filter { !it.checked })
            cursor = page.nextCursor
            check(cursor == null || seen.add(cursor)) { "Repeated Todoist page cursor" }
        } while (cursor != null)
        val rewards = settings.findAll().associateBy { it.taskId.value }
        todos.value = tasks.distinctBy { it.id }.map { task ->
            val reward = rewards[task.id]
            Todo(task.id, task.content, reward?.points ?: 1, task.due?.recurring ?: false)
        }
    }

    override suspend fun update(todo: Todo) = mutex.withLock {
        val saved = settings.update(
            TaskRewardSetting(TodoistTaskId(todo.todoistId), todo.numberOfTicketsObtained),
        )
        todos.value = todos.value.map {
            if (it.todoistId ==
                todo.todoistId
            ) {
                it.copy(numberOfTicketsObtained = saved.points)
            } else {
                it
            }
        }
    }

    override suspend fun complete(todo: Todo) = mutex.withLock {
        if (todos.value.none { it.todoistId == todo.todoistId }) return@withLock
        val response = todoistApi.completeTask(todo.todoistId)
        if (!response.isSuccessful) throw HttpException(response)
        todos.value = todos.value.filterNot { it.todoistId == todo.todoistId }
    }

    override suspend fun deleteAll() = mutex.withLock {
        todos.value = emptyList()
        migration.clear()
    }
}
