package kztproject.jp.splacounter.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import kztproject.jp.splacounter.database.model.Reward

@Database(entities = arrayOf(Reward::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
