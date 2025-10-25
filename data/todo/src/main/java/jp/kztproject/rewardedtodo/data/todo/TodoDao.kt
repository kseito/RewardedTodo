package jp.kztproject.rewardedtodo.data.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoEntity")
    suspend fun findAll(): List<TodoEntity>

    @Query("SELECT * FROM TodoEntity WHERE isDone=0")
    fun findAllAsFlow(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM TodoEntity WHERE todoistId=:todoistId")
    suspend fun findBy(todoistId: Long): TodoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(todoEntity: TodoEntity)

    @Query("UPDATE TodoEntity SET isDone=1 WHERE id=:id ")
    suspend fun completeTaskById(id: Long)

    @Query("UPDATE TodoEntity SET isDone=1 WHERE todoistId=:id ")
    suspend fun completeTaskByTodoistId(id: Long)

    @Delete
    suspend fun delete(todoEntity: TodoEntity)
}
