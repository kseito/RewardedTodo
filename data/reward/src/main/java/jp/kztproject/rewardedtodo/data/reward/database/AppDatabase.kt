package jp.kztproject.rewardedtodo.data.reward.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.kztproject.rewardedtodo.data.reward.database.model.RewardEntity

@Database(entities = arrayOf(RewardEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rewardDao(): RewardDao
}
