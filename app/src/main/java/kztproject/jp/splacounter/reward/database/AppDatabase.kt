package kztproject.jp.splacounter.reward.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import kztproject.jp.splacounter.reward.database.model.Reward

@Database(entities = arrayOf(Reward::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
