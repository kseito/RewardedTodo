package jp.kztproject.rewardedtodo.di.auth

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.application.todo.GetValidAccessTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.RefreshTodoistTokenUseCase
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TodoistApiModule {

    // TODO need to divide
    @Provides
    @Singleton
    fun provideTodoistService(
        getValidAccessTokenUseCase: GetValidAccessTokenUseCase,
        refreshTodoistTokenUseCase: RefreshTodoistTokenUseCase,
    ): TodoistApi {
        // TODO use reflection because codegen is not working.
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient.Builder()
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
            .addInterceptor { chain ->
                // 期限切れならここでリフレッシュされる。未連携ならヘッダを付けずに送る
                val token = runBlocking { getValidAccessTokenUseCase.execute() }
                val request = token
                    ?.let { chain.request().newBuilder().header(AUTHORIZATION_HEADER, "Bearer ${it.value}").build() }
                    ?: chain.request()
                chain.proceed(request)
            }
            .authenticator(TodoistTokenAuthenticator(refreshTodoistTokenUseCase))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.todoist.com")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(TodoistApi::class.java)
    }

    /**
     * 401を受けたときにアクセストークンを再取得してリクエストを1度だけ再送する。
     *
     * 有効期限より先にトークンが失効した場合（Todoist側で連携を取り消した等）の受け皿で、
     * 通常の期限切れはInterceptor側で先回りしてリフレッシュされる。
     */
    private class TodoistTokenAuthenticator(private val refreshTodoistTokenUseCase: RefreshTodoistTokenUseCase) :
        Authenticator {

        override fun authenticate(route: okhttp3.Route?, response: Response): Request? {
            val failedToken = response.request.header(AUTHORIZATION_HEADER) ?: return null

            val refreshedToken = runBlocking { refreshTodoistTokenUseCase.execute().getOrNull() } ?: return null

            val newHeader = "Bearer ${refreshedToken.value}"
            // 同じトークンで送り直しても再び401になるだけなので諦める
            if (newHeader == failedToken) return null

            return response.request.newBuilder()
                .header(AUTHORIZATION_HEADER, newHeader)
                .build()
        }
    }

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
