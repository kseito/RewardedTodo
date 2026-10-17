package jp.kztproject.rewardedtodo.data.todo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Reads only the pre-migration database. Normal Todo operations never persist to SQLite. */
class LegacyTodoMigration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: ITaskRewardRepository,
) {
    suspend fun migrate() = withContext(Dispatchers.IO) {
        val file = context.getDatabasePath(DATABASE_NAME)
        if (!file.exists()) return@withContext
        val rows = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                "SELECT todoistId, numberOfTicketsObtained FROM TodoEntity WHERE todoistId IS NOT NULL",
                null,
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        val points = cursor.getInt(1)
                        require(points in 1..100) { "Invalid legacy task reward; database retained" }
                        add(TaskRewardSetting(TodoistTaskId(cursor.getString(0)), points))
                    }
                }
            }
        }
        settings.import(rows.distinctBy { it.taskId })
        check(context.deleteDatabase(DATABASE_NAME)) { "Could not remove migrated Todo database" }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            check(context.deleteDatabase(DATABASE_NAME)) { "Could not remove legacy Todo database" }
        }
    }

    companion object {
        const val DATABASE_NAME = "rewardedtodo_todo"
    }
}
