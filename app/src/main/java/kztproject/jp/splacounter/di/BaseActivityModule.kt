package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.activity.BaseActivity

@Module
internal abstract class BaseActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    internal abstract fun contributeBaseActivity(): BaseActivity
}
