package kztproject.jp.splacounter.auth.di

import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import project.seito.auth.BuildConfig
import project.seito.screen_transition.di.FragmentScope
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AuthApiModule {

    @Provides
    @FragmentScope
    fun provideTodoistService(): TodoistService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.TODOIST_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoistService::class.java)
    }

    @Provides
    @FragmentScope
    fun provideRewardListLoginService(): RewardListLoginService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardListLoginService::class.java)
    }
}