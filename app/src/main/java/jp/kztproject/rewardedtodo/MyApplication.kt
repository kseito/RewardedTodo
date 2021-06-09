package jp.kztproject.rewardedtodo

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import jp.kztproject.rewardedtodo.di.AppComponent
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private val appComponent: AppComponent by lazy {
        EntryPoints.get(this, AppComponent::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}
