package jp.kztproject.rewardedtodo.data.todo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LegacyTodoMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val settings = mockk<ITaskRewardRepository>(relaxed = true)
    private val migration = LegacyTodoMigration(context, settings)

    @After
    fun cleanup() {
        context.deleteDatabase(LegacyTodoMigration.DATABASE_NAME)
    }

    private fun createLegacyDatabase(points: Int = 3) {
        context.openOrCreateDatabase(LegacyTodoMigration.DATABASE_NAME, 0, null).use {
            it.execSQL("CREATE TABLE TodoEntity (todoistId TEXT, numberOfTicketsObtained INTEGER)")
            it.execSQL("INSERT INTO TodoEntity VALUES ('task', $points)")
            it.execSQL("INSERT INTO TodoEntity VALUES (NULL, 5)")
        }
    }

    @Test
    fun `successful import removes database and runs only once`() = runTest {
        createLegacyDatabase()
        migration.migrate()
        migration.migrate()
        coVerify(exactly = 1) { settings.import(listOf(TaskRewardSetting(TodoistTaskId("task"), 3))) }
        context.getDatabasePath(LegacyTodoMigration.DATABASE_NAME).exists() shouldBe false
    }

    @Test
    fun `failed import preserves database for retry`() = runTest {
        createLegacyDatabase()
        coEvery { settings.import(any()) } throws IllegalStateException("offline")
        shouldThrow<IllegalStateException> { migration.migrate() }
        context.getDatabasePath(LegacyTodoMigration.DATABASE_NAME).exists() shouldBe true
    }

    @Test
    fun `invalid legacy value preserves database`() = runTest {
        createLegacyDatabase(101)
        shouldThrow<IllegalArgumentException> { migration.migrate() }
        context.getDatabasePath(LegacyTodoMigration.DATABASE_NAME).exists() shouldBe true
        coVerify(exactly = 0) { settings.import(any()) }
    }
}
