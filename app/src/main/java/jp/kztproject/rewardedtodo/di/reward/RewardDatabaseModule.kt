package jp.kztproject.rewardedtodo.di.reward

import android.app.Application
import dagger.Module
import dagger.Provides
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.di.scope.ActivityScope
import jp.kztproject.rewardedtodo.reward.infrastructure.database.AppDatabase
import jp.kztproject.rewardedtodo.reward.infrastructure.database.RewardDao

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