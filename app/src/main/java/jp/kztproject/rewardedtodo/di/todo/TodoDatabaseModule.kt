package jp.kztproject.rewardedtodo.di.todo

import android.app.Application
import dagger.Module
import dagger.Provides
import jp.kztproject.rewardedtodo.common.database.DatabaseInitializer
import jp.kztproject.rewardedtodo.data.todo.AppDatabase
import jp.kztproject.rewardedtodo.data.todo.TodoDao
import jp.kztproject.rewardedtodo.di.scope.ActivityScope

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