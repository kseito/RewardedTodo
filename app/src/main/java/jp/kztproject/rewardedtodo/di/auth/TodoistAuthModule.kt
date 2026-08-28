package jp.kztproject.rewardedtodo.di.auth

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.data.todoist.TodoistAuthApi
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
import jp.kztproject.rewardedtodo.domain.todo.TodoistOAuthConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * TodoistのOAuthエンドポイント用のDI。
 *
 * [TodoistAuthApi] にはアクセストークンを付与しない。トークン取得のための通信であり、
 * `TodoistApiModule` のInterceptorと相互依存させないためにクライアントを分けている。
 */
@InstallIn(SingletonComponent::class)
@Module
class TodoistAuthModule {

    @Provides
    @Singleton
    fun provideTodoistOAuthConfig(): TodoistOAuthConfig = TodoistOAuthConfig(
        clientId = BuildConfig.TODOIST_CLIENT_ID,
        authorizeUrl = BuildConfig.TODOIST_AUTHORIZE_URL,
        redirectUri = BuildConfig.TODOIST_REDIRECT_URI,
        scope = BuildConfig.TODOIST_SCOPE,
    )

    @Provides
    @Singleton
    fun provideCurrentTimeProvider(): CurrentTimeProvider = CurrentTimeProvider { System.currentTimeMillis() }

    @Provides
    @Singleton
    fun provideTodoistAuthApi(): TodoistAuthApi {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            // アクセストークンとリフレッシュトークンがログに出ないようボディは出力しない
                            level = HttpLoggingInterceptor.Level.BASIC
                        },
                    )
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.todoist.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TodoistAuthApi::class.java)
    }
}
