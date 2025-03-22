package jp.kztproject.rewardedtodo.data.todo.repository

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.data.todo.AppDatabase
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import jp.kztproject.rewardedtodo.data.todo.TodoEntity
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.data.todoist.model.Due
import jp.kztproject.rewardedtodo.data.todoist.model.Task
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
    private val preferences = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)

    private val repository = TodoRepository(dao, api, preferences)

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

        val tasks = listOf(
            Task(101, "test_content", false, Due(true)),
            Task(102, "test_content", false, Due(true)),
            Task(103, "test_content", false, Due(false)),
        )
        coEvery { api.fetchTasks(any()) } returns tasks

        withContext(Dispatchers.IO) {
            repository.sync()
            val actual = repository.findAll().first()
            assertThat(actual.size).isEqualTo(3)
            actual[0].run {
                assertThat(this.id).isEqualTo(1)
                assertThat(this.todoistId).isEqualTo(101)
                assertThat(this.isRepeat).isTrue()
            }
            actual[1].run {
                assertThat(this.id).isEqualTo(2)
                assertThat(this.todoistId).isEqualTo(102)
                assertThat(this.isRepeat).isTrue()
            }
            actual[2].run {
                assertThat(this.id).isEqualTo(3)
                assertThat(this.todoistId).isEqualTo(103)
                assertThat(this.isRepeat).isFalse()
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
                    101,
                    "test_name1",
                    1,
                    isRepeat = true,
                    isDone = false,
                ),
            )
            dao.insertOrUpdate(
                TodoEntity(
                    2,
                    102,
                    "test_name2",
                    1,
                    isRepeat = true,
                    isDone = true,
                ),
            )
            dao.insertOrUpdate(
                TodoEntity(
                    3,
                    103,
                    "test_name3",
                    1,
                    isRepeat = true,
                    isDone = false,
                ),
            )
        }
        val tasks = listOf(
            Task(101, "test_content1", false, Due(true)),
            Task(102, "test_content2", false, Due(true)),
        )
        coEvery { api.fetchTasks(any()) } returns tasks

        withContext(Dispatchers.IO) {
            repository.sync()
            val actual = repository.findAll().first()

            assertThat(actual.size).isEqualTo(2)
            actual[0].run {
                assertThat(this.id).isEqualTo(1)
                assertThat(this.todoistId).isEqualTo(101)
                assertThat(this.name).isEqualTo("test_content1")
            }
            actual[1].run {
                assertThat(this.id).isEqualTo(2)
                assertThat(this.todoistId).isEqualTo(102)
                assertThat(this.name).isEqualTo("test_content2")
            }
        }
    }

    private fun useTodoist(flag: Boolean) {
        if (flag) {
            preferences.edit().putString(EncryptedStore.TODOIST_ACCESS_TOKEN, "test_token").apply()
        }
    }
}
