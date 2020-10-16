package kztproject.jp.splacounter.data.todo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
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
    fun insertReward() {
        val dao = database.todoDao()
        dummyTodoList.forEach { dao.insertOrUpdate(it) }

        val actual = dao.findAll()
        assertThat(actual.size).isEqualTo(3)
        actual[0].run {
            assertThat(id).isEqualTo(1)
            assertThat(name).isEqualTo("test 1")
            assertThat(numberOfTicketsObtained).isEqualTo(1.0f)
            assertThat(isRepeat).isTrue()
        }
    }
}