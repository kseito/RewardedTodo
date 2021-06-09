package jp.kztproject.rewardedtodo.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import project.seito.screen_transition.IFragmentsTransitionManager
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Named
import javax.inject.Singleton

@Module
internal class AppModule {

    @Provides
    @Singleton
    fun providesPrefsWrapper(@ApplicationContext context: Context): PrefsWrapper = PrefsWrapper(context)

    @Provides
    @Singleton
    @Named("default")
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    @Named("encrypted")
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
                EncryptedStore.FILE_NAME,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun providesFragmentsInitializer(fragmentsInitializer: FragmentsTransitionManager): IFragmentsTransitionManager = fragmentsInitializer
}
