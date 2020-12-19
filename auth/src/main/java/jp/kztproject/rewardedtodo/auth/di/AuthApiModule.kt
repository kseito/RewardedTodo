package jp.kztproject.rewardedtodo.auth.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import jp.kztproject.rewardedtodo.auth.api.RewardListLoginService
import jp.kztproject.rewardedtodo.auth.api.TodoistService
import project.seito.auth.BuildConfig
import project.seito.screen_transition.di.FragmentScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AuthApiModule {

    @Provides
    @FragmentScope
    fun provideTodoistService(): TodoistService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.TODOIST_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoistService::class.java)
    }

    @Provides
    @FragmentScope
    fun provideRewardListLoginService(): RewardListLoginService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardListLoginService::class.java)
    }
}