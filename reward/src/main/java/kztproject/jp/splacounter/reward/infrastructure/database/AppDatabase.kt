package kztproject.jp.splacounter.reward.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

@Database(entities = arrayOf(RewardEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
