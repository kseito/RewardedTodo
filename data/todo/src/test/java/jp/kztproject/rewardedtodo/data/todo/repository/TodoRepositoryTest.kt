package jp.kztproject.rewardedtodo.data.todo.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.todo.LegacyTodoMigration
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.data.todoist.model.Tasks
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class TodoRepositoryTest {
    private val api = mockk<TodoistApi>()
    private val settings = mockk<ITaskRewardRepository>()
    private val migration = mockk<LegacyTodoMigration>(relaxed = true)

    private val repository = TodoRepository(api, settings, migration)
    private val task = Task("abc", "Example", false, null)

    private fun stubList() {
        coEvery { api.fetchTasks("today|overdue", null) } returns Tasks(listOf(task))
        coEvery { settings.findAll() } returns listOf(TaskRewardSetting(TodoistTaskId("abc"), 3, 1))
    }

    @Test
    fun `joins all pages with server rewards and defaults only absent settings`() = runTest {
        stubList()
        coEvery { api.fetchTasks("today|overdue", null) } returns Tasks(listOf(task), "next")
        coEvery { api.fetchTasks("today|overdue", "next") } returns Tasks(listOf(task.copy(id = "def")))
        repository.sync()
        repository.findAll().first().map { it.numberOfTicketsObtained } shouldBe listOf(3, 1)
        repository.findAll().first().first().rewardVersion shouldBe 1
        coVerify(exactly = 1) { migration.migrate() }
    }

    @Test
    fun `failed settings fetch does not replace existing values with defaults`() = runTest {
        stubList()
        repository.sync()
        coEvery { settings.findAll() } throws IllegalStateException("offline")
        shouldThrow<IllegalStateException> { repository.sync() }
        repository.findAll().first().single().numberOfTicketsObtained shouldBe 3
    }

    @Test
    fun `failed migration prevents fetching and completing an uninitialized list`() = runTest {
        stubList()
        coEvery { migration.migrate() } throws IllegalStateException("offline")
        shouldThrow<IllegalStateException> { repository.sync() }
        repository.findAll().first() shouldBe emptyList()
        coVerify(exactly = 0) { api.fetchTasks(any(), any()) }
    }

    @Test
    fun `failed save preserves state and success uses server version`() = runTest {
        stubList()
        repository.sync()
            val todo = repository.findAll().first().single()
        coEvery { settings.update(any()) } throws IllegalStateException("offline")
        shouldThrow<IllegalStateException> { repository.update(todo.copy(numberOfTicketsObtained = 7)) }
        repository.findAll().first().single() shouldBe todo
        coEvery { settings.update(TaskRewardSetting(TodoistTaskId("abc"), 7, 1)) } returns
            TaskRewardSetting(TodoistTaskId("abc"), 7, 2)
        repository.update(todo.copy(numberOfTicketsObtained = 7))
        repository.findAll().first().single().rewardVersion shouldBe 2
        repository.findAll().first().single().numberOfTicketsObtained shouldBe 7
    }

    @Test
    fun `failed completion retains task and successful completion suppresses repeated taps`() = runTest {
        stubList()
        repository.sync()
            val todo = repository.findAll().first().single()
        coEvery { api.completeTask("abc") } returns Response.error(500, "failure".toResponseBody())
        shouldThrow<HttpException> { repository.complete(todo) }
        repository.findAll().first().single() shouldBe todo
        coEvery { api.completeTask("abc") } returns Response.success(Unit)
        repository.complete(todo)
        repository.complete(todo)
        repository.findAll().first() shouldBe emptyList()
        coVerify(exactly = 2) { api.completeTask("abc") }
    }

    @Test
    fun `account reset clears memory and legacy database`() = runTest {
        stubList()
        repository.sync()
        repository.deleteAll()
        repository.findAll().first() shouldBe emptyList()
        coVerify(exactly = 1) { migration.clear() }
    }
}
