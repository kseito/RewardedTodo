package jp.kztproject.rewardedtodo.di.reward

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.data.reward.database.AppDatabase
import jp.kztproject.rewardedtodo.data.reward.database.RewardDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RewardDatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application): AppDatabase =
        DatabaseInitializer.init(application, AppDatabase::class.java, "reward")

    @Provides
    fun providesRewardDao(appDatabase: AppDatabase): RewardDao = appDatabase.rewardDao()
}
