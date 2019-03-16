package kztproject.jp.splacounter.reward.di

import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.reward.database.AppDatabase
import kztproject.jp.splacounter.reward.database.RewardDao

@Module
class RewardDatabaseModule {

    @Provides
    fun providesRewardDao(appDatabase: AppDatabase): RewardDao {
        return appDatabase.rewardDao()
    }
}