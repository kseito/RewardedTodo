package jp.kztproject.rewardedtodo.common.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseInitializer {
    fun <T : RoomDatabase> init(context: Context, cls: Class<T>, name: String): T {
        return Room.databaseBuilder(context, cls, "rewardedtodo_$name")
                .fallbackToDestructiveMigration()
                .build()
    }
}