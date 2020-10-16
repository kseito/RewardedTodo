package kztproject.jp.splacounter.data.todo

import androidx.room.*

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoEntity")
    fun findAll(): List<TodoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(todoEntity: TodoEntity)

    @Delete
    fun delete(todoEntity: TodoEntity)
}