package kztproject.jp.splacounter.di.reward

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.reward.infrastructure.api.RewardPointService
import project.seito.reward.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class PointApiModule {

    @Provides
    fun provideRewardPointService(): RewardPointService {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.REWARD_LIST_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RewardPointService::class.java)
    }
}