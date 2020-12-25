package jp.kztproject.rewardedtodo.reward.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.kztproject.rewardedtodo.reward.infrastructure.database.model.RewardEntity

@Database(entities = arrayOf(RewardEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
