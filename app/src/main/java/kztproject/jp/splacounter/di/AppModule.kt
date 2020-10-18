package kztproject.jp.splacounter.di

import android.app.Application
import dagger.Module
import dagger.Provides
import project.seito.screen_transition.IFragmentsTransitionManager
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun providesPrefsWrapper(application: Application): PrefsWrapper = PrefsWrapper(application.applicationContext)

    @Provides
    @Singleton
    fun providesFragmentsInitializer(fragmentsInitializer: FragmentsTransitionManager): IFragmentsTransitionManager = fragmentsInitializer
}
