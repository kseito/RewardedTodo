package jp.kztproject.rewardedtodo.common.database

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseInitializer {
    fun <T : RoomDatabase> init(application: Application, cls: Class<T>, name: String): T {
        return Room.databaseBuilder(application, cls, "rewardedtodo_$name")
                .fallbackToDestructiveMigration()
                .build()
    }
}