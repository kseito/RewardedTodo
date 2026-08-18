package jp.kztproject.rewardedtodo.data.todo.repository

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.data.todo.AppDatabase
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import jp.kztproject.rewardedtodo.data.todo.TodoEntity
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Due
import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.data.todoist.model.Tasks
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class TodoRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private val dao: TodoDao =
        DatabaseInitializer.init(applicationContext, AppDatabase::class.java, "todo").todoDao()
    private val api: TodoistApi = mockk()
    private val apiTokenRepository: IApiTokenRepository = mockk()

    private val repository = TodoRepository(dao, api, apiTokenRepository)

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun takeInTasksFromTodoist() = runTest {
        useTodoist(true)

        val tasks = Tasks(
            listOf(
                Task("101", "test_content", false, Due(true)),
                Task("102", "test_content", false, Due(true)),
                Task("103", "test_content", false, Due(false)),
            ),
        )
        coEvery { api.fetchTasks(any()) } returns tasks

        withContext(Dispatchers.IO) {
            repository.sync()
            val actual = repository.findAll().first()
            actual.size shouldBe 3
            actual[0].run {
                this.id shouldBe 1
                this.todoistId shouldBe "101"
                this.isRepeat shouldBe true
            }
            actual[1].run {
                this.id shouldBe 2
                this.todoistId shouldBe "102"
                this.isRepeat shouldBe true
            }
            actual[2].run {
                this.id shouldBe 3
                this.todoistId shouldBe "103"
                this.isRepeat shouldBe false
            }
        }
    }

    @Test
    fun takeInTasksWithNullDueFromTodoist() = runTest {
        useTodoist(true)

        val tasks = Tasks(
            listOf(
                Task("101", "test_content_with_due", false, Due(true)),
                Task("102", "test_content_without_due", false, null),
                Task("103", "test_content_with_non_recurring_due", false, Due(false)),
            ),
        )
        coEvery { api.fetchTasks(any()) } returns tasks

        withContext(Dispatchers.IO) {
            repository.sync()
            val actual = repository.findAll().first()
            actual.size shouldBe 3
            actual[0].run {
                this.id shouldBe 1
                this.todoistId shouldBe "101"
                this.name shouldBe "test_content_with_due"
                this.isRepeat shouldBe true
            }
            actual[1].run {
                this.id shouldBe 2
                this.todoistId shouldBe "102"
                this.name shouldBe "test_content_without_due"
                this.isRepeat shouldBe false
            }
            actual[2].run {
                this.id shouldBe 3
                this.todoistId shouldBe "103"
                this.name shouldBe "test_content_with_non_recurring_due"
                this.isRepeat shouldBe false
            }
        }
    }

    @Test
    fun ignoreTaskFromTodoist() = runTest {
        useTodoist(true)

        withContext(Dispatchers.IO) {
            dao.insertOrUpdate(
                TodoEntity(
                    1,
                    "101",
                    "test_name1",
                    1,
                    isRepeat = true,
                    isDone = false,
                ),
            )
            dao.insertOrUpdate(
                TodoEntity(
                    2,
                    "102",
                    "test_name2",
                    1,
                    isRepeat = true,
                    isDone = true,
                ),
            )
            dao.insertOrUpdate(
                TodoEntity(
                    3,
                    "103",
                    "test_name3",
                    1,
                    isRepeat = true,
                    isDone = false,
                ),
            )
        }
        val tasks = Tasks(
            listOf(
                Task("101", "test_content1", false, Due(true)),
                Task("102", "test_content2", false, Due(true)),
            ),
        )
        coEvery { api.fetchTasks(any()) } returns tasks

        withContext(Dispatchers.IO) {
            repository.sync()
            val actual = repository.findAll().first()

            actual.size shouldBe 2
            actual[0].run {
                this.id shouldBe 1
                this.todoistId shouldBe "101"
                this.name shouldBe "test_content1"
            }
            actual[1].run {
                this.id shouldBe 2
                this.todoistId shouldBe "102"
                this.name shouldBe "test_content2"
            }
        }
    }

    private fun useTodoist(flag: Boolean) {
        val token = if (flag) {
            ApiToken.create("0123456789abcdef0123456789abcdef01234567")
        } else {
            null
        }
        coEvery { apiTokenRepository.getToken() } returns token
    }
}
