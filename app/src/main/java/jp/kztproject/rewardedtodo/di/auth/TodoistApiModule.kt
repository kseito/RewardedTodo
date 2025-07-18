package jp.kztproject.rewardedtodo.di.auth

import android.content.SharedPreferences
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
class TodoistApiModule {

    // TODO need to divide
    @Provides
    fun provideTodoistService(@Named("encrypted") preferences: SharedPreferences): TodoistApi {
        // TODO use reflection because codegen is not working.
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val token = preferences.getString(EncryptedStore.TODOIST_ACCESS_TOKEN, "")
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
