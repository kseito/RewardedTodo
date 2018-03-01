package kztproject.jp.splacounter

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import kztproject.jp.splacounter.di.AppComponent
import kztproject.jp.splacounter.di.DaggerAppComponent
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class MyApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        PrefsWrapper.initialize(applicationContext)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    fun component(): AppComponent {
        return appComponent
    }
}
