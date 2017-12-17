package kztproject.jp.splacounter

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.Component
import kztproject.jp.splacounter.di.AppComponent
import kztproject.jp.splacounter.di.AppModule
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Singleton

class MyApplication : Application() {

    private var appComponent: AppComponent? = null

    @Singleton
    @Component(modules = arrayOf(AppModule::class))
    interface AppAppComponent : AppComponent

    override fun onCreate() {
        super.onCreate()

        if (appComponent == null) {

            appComponent = DaggerMyApplication_AppAppComponent.builder()
                    .appModule(AppModule(this))
                    .build()
        }

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        PrefsWrapper.initialize(applicationContext)
    }

    fun component(): AppComponent? {
        return appComponent
    }
}
