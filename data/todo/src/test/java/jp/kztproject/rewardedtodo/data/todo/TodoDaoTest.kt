package jp.kztproject.rewardedtodo.data.todo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.kotest.matchers.shouldBe
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
        TodoEntity(3, "103", "test 3", 1, isRepeat = true, isDone = false),
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
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
        actual.size shouldBe 3
        actual[0].run {
            id shouldBe 1
            name shouldBe "test 1"
            numberOfTicketsObtained shouldBe 1
            isRepeat shouldBe true
        }
    }

    @Test
    fun deleteTodo() = runTest {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        dao.delete(dummyTodoList[0])

        val todoList = dao.findAll()
        todoList.size shouldBe 2
        todoList[0].id shouldBe 2
        todoList[1].id shouldBe 3
    }

    @Test
    fun update() = runTest {
        val dao = database.todoDao()
        dao.insertOrUpdate(dummyTodoList[0])
        dao.insertOrUpdate(dummyTodoList[0].copy(name = "test 1 Copy"))
        val actual = dao.findAll()[0]
        actual shouldBe dummyTodoList[0].copy(name = "test 1 Copy")
    }
}
