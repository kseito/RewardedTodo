package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.BaseActivity
import kztproject.jp.splacounter.auth.di.AuthModule

@Module
internal abstract class BaseActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class, AuthModule::class])
    internal abstract fun contributeBaseActivity(): BaseActivity
}
