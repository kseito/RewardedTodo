package jp.kztproject.rewardedtodo.data.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoEntity")
    fun findAll(): List<TodoEntity>

    @Query("SELECT * FROM TodoEntity WHERE isDone=0")
    fun findAllAsFlow(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(todoEntity: TodoEntity)

    @Query("UPDATE TodoEntity SET isDone=1 WHERE todoistId=:id ")
    fun completeTask(id: Long)

    @Delete
    fun delete(todoEntity: TodoEntity)
}