package kztproject.jp.splacounter.data.todo

import androidx.room.RoomDatabase

abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}