package kztproject.jp.splacounter.common.database

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseInitializer {
    fun <T : RoomDatabase> init(application: Application, cls: Class<T>): T {
        return Room.databaseBuilder(application, cls, "splacounter")
                .fallbackToDestructiveMigration()
                .build()
    }
}