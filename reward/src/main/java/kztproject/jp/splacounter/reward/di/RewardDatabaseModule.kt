package kztproject.jp.splacounter.reward.di

import android.app.Application
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.common.database.DatabaseInitializer
import kztproject.jp.splacounter.reward.infrastructure.database.AppDatabase
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao
import javax.inject.Singleton

@Module
class RewardDatabaseModule {

    @Provides
    fun providesAppDatabase(application: Application): AppDatabase {
        return DatabaseInitializer.init(application, AppDatabase::class.java)
    }

    @Provides
    fun providesRewardDao(appDatabase: AppDatabase): RewardDao {
        return appDatabase.rewardDao()
    }
}