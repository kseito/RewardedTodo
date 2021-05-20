package jp.kztproject.rewardedtodo.di.auth

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.data.auth.TodoistApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class TodoistApiModule {

    @Provides
    fun provideTodoistService(): TodoistApi {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.TODOIST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(TodoistApi::class.java)
    }
}