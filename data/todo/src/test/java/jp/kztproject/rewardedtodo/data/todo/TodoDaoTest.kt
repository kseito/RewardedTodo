package jp.kztproject.rewardedtodo.data.todo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TodoDaoTest {

    private lateinit var database: AppDatabase
    private val dummyTodoList = listOf(
        TodoEntity(1, "101", "test 1", 1, isRepeat = true, isDone = false),
        TodoEntity(2, "102", "test 2", 2, isRepeat = true, isDone = false),
        TodoEntity(3, "103", "test 3", 1, isRepeat = true, isDone = false)
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndFindTodo() = runTest {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        val actual = dao.findAllAsFlow().take(1).first()
        assertThat(actual.size).isEqualTo(3)
        actual[0].run {
            assertThat(id).isEqualTo(1)
            assertThat(name).isEqualTo("test 1")
            assertThat(numberOfTicketsObtained).isEqualTo(1)
            assertThat(isRepeat).isTrue()
        }
    }

    @Test
    fun deleteTodo() = runTest {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        dao.delete(dummyTodoList[0])

        val todoList = dao.findAll()
        assertThat(todoList.size).isEqualTo(2)
        assertThat(todoList[0].id).isEqualTo(2)
        assertThat(todoList[1].id).isEqualTo(3)
    }

    @Test
    fun update() = runTest {
        val dao = database.todoDao()
        dao.insertOrUpdate(dummyTodoList[0])
        dao.insertOrUpdate(dummyTodoList[0].copy(name = "test 1 Copy"))
        val actual = dao.findAll()[0]
        assertThat(actual).isEqualTo(dummyTodoList[0].copy(name = "test 1 Copy"))
    }
}
