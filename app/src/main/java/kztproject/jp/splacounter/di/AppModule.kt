package kztproject.jp.splacounter.di

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.BuildConfig
import kztproject.jp.splacounter.auth.preference.PrefsWrapper
import kztproject.jp.splacounter.reward.api.RewardPointService
import kztproject.jp.splacounter.reward.database.AppDatabase
import kztproject.jp.splacounter.reward.database.RewardDao
import project.seito.screen_transition.IFragmentsInitializer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun provideRewardPointService(): RewardPointService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardPointService::class.java)
    }

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "splacounter")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun providesRewardDao(appDatabase: AppDatabase): RewardDao {
        return appDatabase.rewardDao()
    }

    @Provides
    @Singleton
    fun providesPrefsWrapper(application: Application): PrefsWrapper = PrefsWrapper(application.applicationContext)

    @Provides
    @Singleton
    fun providesFragmentsInitializer(fragmentsInitializer: FragmentsInitializer): IFragmentsInitializer = fragmentsInitializer
}
