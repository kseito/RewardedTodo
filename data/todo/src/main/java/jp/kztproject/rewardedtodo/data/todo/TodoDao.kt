package jp.kztproject.rewardedtodo.data.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoEntity")
    fun findAll(): List<TodoEntity>

    @Query("SELECT * FROM TodoEntity WHERE isDone=0")
    fun findAllAsFlow(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM TodoEntity WHERE todoistId=:todoistId")
    fun findBy(todoistId: Long): TodoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(todoEntity: TodoEntity)

    @Query("UPDATE TodoEntity SET isDone=1 WHERE id=:id ")
    fun completeTaskById(id: Long)

    @Query("UPDATE TodoEntity SET isDone=1 WHERE todoistId=:id ")
    fun completeTaskByTodoistId(id: Long)

    @Delete
    fun delete(todoEntity: TodoEntity)
}