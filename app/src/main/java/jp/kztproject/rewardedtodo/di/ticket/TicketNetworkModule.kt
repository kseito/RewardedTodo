package jp.kztproject.rewardedtodo.di.ticket

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardIdentityApi
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardServerApi
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import javax.inject.Named
import jp.kztproject.rewardedtodo.data.reward.network.RewardApi
import jp.kztproject.rewardedtodo.application.todo.GetValidAccessTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.exception.TodoistUnauthorizedException

@InstallIn(SingletonComponent::class)
@Module
class TicketNetworkModule {

    @Provides
    @Singleton
    @Named("rewardIdentity")
    fun provideRewardIdentityRetrofit(getValidAccessTokenUseCase: GetValidAccessTokenUseCase): Retrofit {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                if (chain.request().header("Authorization") !=
                    null
                ) {
                    return@addInterceptor chain.proceed(chain.request())
                }
                val token = runBlocking { getValidAccessTokenUseCase.execute() }
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
    }

    @Provides
    @Singleton
    fun provideRewardIdentityApi(@Named("rewardIdentity") retrofit: Retrofit): RewardIdentityApi =
        retrofit.create(RewardIdentityApi::class.java)

    @Provides
    @Singleton
    @Named("rewardServer")
    fun provideRewardServerRetrofit(
        @Named("rewardIdentity") identityRetrofit: Retrofit,
        users: RewardUserIdRepository,
    ): Retrofit {
        val client = (identityRetrofit.callFactory() as OkHttpClient).newBuilder()
            // 401の処理中にユーザーIDを解決する通信は、別のDispatcherで実行する。
            .dispatcher(Dispatcher())
            .authenticator(RewardServerAuthenticator(identityRetrofit.baseUrl(), users::refreshUserId))
            .build()
        return identityRetrofit.newBuilder().client(client).build()
    }

    @Provides
    @Singleton
    fun provideRewardServerApi(@Named("rewardServer") retrofit: Retrofit): RewardServerApi =
        retrofit.create(RewardServerApi::class.java)

    @Provides
    @Singleton
    fun provideRewardApi(@Named("rewardServer") retrofit: Retrofit): RewardApi = retrofit.create(RewardApi::class.java)
}
