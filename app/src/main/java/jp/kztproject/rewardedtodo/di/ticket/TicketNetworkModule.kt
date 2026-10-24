package jp.kztproject.rewardedtodo.di.ticket

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import jp.kztproject.rewardedtodo.application.todo.GetValidAccessTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.exception.TodoistUnauthorizedException

@InstallIn(SingletonComponent::class)
@Module
class TicketNetworkModule {

    @Provides
    @Singleton
    fun provideRewardServerApi(getValidAccessTokenUseCase: GetValidAccessTokenUseCase): RewardServerApi {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = kotlinx.coroutines.runBlocking { getValidAccessTokenUseCase.execute() }
                    ?: throw TodoistUnauthorizedException()
                chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer ${token.value}").build())
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            redactHeader("Authorization")
                            redactHeader("Cookie")
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.REWARD_SERVER_URL + "/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RewardServerApi::class.java)
    }
}
