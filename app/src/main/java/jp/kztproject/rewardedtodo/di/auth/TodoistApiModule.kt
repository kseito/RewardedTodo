package jp.kztproject.rewardedtodo.di.auth

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
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
    fun provideTodoistService(apiTokenRepository: IApiTokenRepository): TodoistApi {
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
                val token = runBlocking {
                    apiTokenRepository.getToken()?.value.orEmpty()
                }
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.todoist.com")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(TodoistApi::class.java)
    }
}
