package kztproject.jp.splacounter.data.todo.di

import android.app.Application
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.common.database.DatabaseInitializer
import kztproject.jp.splacounter.data.todo.AppDatabase
import kztproject.jp.splacounter.data.todo.TodoDao

@Module
class TodoDatabaseModule {

    @Provides
    fun providesAppDatabase(application: Application): AppDatabase {
        return DatabaseInitializer.init(application, AppDatabase::class.java, "todo")
    }

    @Provides
    fun providesTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }
}