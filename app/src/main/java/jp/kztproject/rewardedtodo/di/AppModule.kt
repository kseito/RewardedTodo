package jp.kztproject.rewardedtodo.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import project.seito.screen_transition.IFragmentsTransitionManager
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Named
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun providesPrefsWrapper(application: Application): PrefsWrapper = PrefsWrapper(application.applicationContext)

    @Provides
    @Singleton
    @Named("default")
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
    }

    @Provides
    @Singleton
    @Named("encrypted")
    fun provideEncryptedSharedPreferences(application: Application): SharedPreferences {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
                EncryptedStore.FILE_NAME,
                masterKey,
                application.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun providesFragmentsInitializer(fragmentsInitializer: FragmentsTransitionManager): IFragmentsTransitionManager = fragmentsInitializer
}
