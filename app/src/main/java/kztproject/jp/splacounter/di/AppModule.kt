package kztproject.jp.splacounter.di

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.BuildConfig
import kztproject.jp.splacounter.api.RewardListClient
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.database.AppDatabase
import kztproject.jp.splacounter.database.RewardDao
import kztproject.jp.splacounter.exception.InvalidUrlException
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun provideTodoistService(): TodoistService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.TODOIST_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoistService::class.java)
    }

    @Provides
    @Singleton
    fun provideRewardListClient(): RewardListClient {
        val url = HttpUrl.parse(BuildConfig.REWARD_LIST_SERVER_URL)
        return url?.let { RewardListClient(it) } ?: throw InvalidUrlException()
    }

    @Provides
    @Singleton
    fun provideRewardListLoginService(): RewardListLoginService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardListLoginService::class.java)
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
}
