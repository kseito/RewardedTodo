package jp.kztproject.rewardedtodo.di.todo

import android.app.Application
import android.view.View
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.data.todo.AppDatabase
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TodoDatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application): AppDatabase {
        return DatabaseInitializer.init(application, AppDatabase::class.java, "todo")
    }

    @Provides
    fun providesTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }
}
