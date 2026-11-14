package jp.kztproject.rewardedtodo

import android.app.Application
import androidx.core.content.edit
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val cleanup = getSharedPreferences("database_cleanup", MODE_PRIVATE)
        if (!cleanup.getBoolean("reward_removed", false)) {
            if (!getDatabasePath("reward").exists() || deleteDatabase("reward")) {
                cleanup.edit { putBoolean("reward_removed", true) }
            }
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
