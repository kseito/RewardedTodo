package kztproject.jp.splacounter.reward.di

import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.reward.api.RewardPointService
import project.seito.reward.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class PointApiModule {

    @Provides
    fun provideRewardPointService(): RewardPointService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RewardPointService::class.java)
    }
}