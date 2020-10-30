package kztproject.jp.splacounter.di.reward

import android.app.Application
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.common.database.DatabaseInitializer
import kztproject.jp.splacounter.reward.di.ActivityScope
import kztproject.jp.splacounter.reward.infrastructure.database.AppDatabase
import kztproject.jp.splacounter.reward.infrastructure.database.RewardDao

@Module
class RewardDatabaseModule {

    @Provides
    @ActivityScope
    fun providesAppDatabase(application: Application): AppDatabase {
        return DatabaseInitializer.init(application, AppDatabase::class.java, "reward")
    }

    @Provides
    fun providesRewardDao(appDatabase: AppDatabase): RewardDao {
        return appDatabase.rewardDao()
    }
}