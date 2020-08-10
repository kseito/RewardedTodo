package kztproject.jp.splacounter.reward.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kztproject.jp.splacounter.reward.database.RewardDao
import kztproject.jp.splacounter.reward.database.model.Reward

@Database(entities = arrayOf(Reward::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
