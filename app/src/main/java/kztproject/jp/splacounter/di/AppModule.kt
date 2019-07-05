package kztproject.jp.splacounter.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import kztproject.jp.splacounter.reward.database.AppDatabase
import project.seito.screen_transition.IFragmentsTransitionManager
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "splacounter")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun providesPrefsWrapper(application: Application): PrefsWrapper = PrefsWrapper(application.applicationContext)

    @Provides
    @Singleton
    fun providesFragmentsInitializer(fragmentsInitializer: FragmentsTransitionManager): IFragmentsTransitionManager = fragmentsInitializer
}
