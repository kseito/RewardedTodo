package kztproject.jp.splacounter.data.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoEntity")
    fun findAll(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(todoEntity: TodoEntity)

    @Delete
    fun delete(todoEntity: TodoEntity)
}