package jp.kztproject.rewardedtodo.data.todo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TodoDaoTest {

    private lateinit var database: AppDatabase
    private val dummyTodoList = listOf<TodoEntity>(
            TodoEntity(1, "test 1", 1.0f, true),
            TodoEntity(2, "test 2", 2.0f, true),
            TodoEntity(3, "test 3", 1.2f, true)
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndFindTodo() = runBlocking {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        val actual = dao.findAll().take(1).first()
        assertThat(actual.size).isEqualTo(3)
        actual[0].run {
            assertThat(id).isEqualTo(1)
            assertThat(name).isEqualTo("test 1")
            assertThat(numberOfTicketsObtained).isEqualTo(1.0f)
            assertThat(isRepeat).isTrue()
        }
    }

    @Test
    fun deleteTodo() = runBlocking {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        dao.delete(dummyTodoList[0])

        val todoList = dao.findAll().take(1).first()
        assertThat(todoList.size).isEqualTo(2)
        assertThat(todoList[0].id).isEqualTo(2)
        assertThat(todoList[1].id).isEqualTo(3)
    }

    @Test
    fun update() = runBlocking {
        val dao = database.todoDao()
        dao.insertOrUpdate(dummyTodoList[0])
        dao.insertOrUpdate(dummyTodoList[0].copy(name = "test 1 Copy"))
        val actual = dao.findAll().take(1).first()[0]
        assertThat(actual).isEqualTo(dummyTodoList[0].copy(name = "test 1 Copy"))


    }
}