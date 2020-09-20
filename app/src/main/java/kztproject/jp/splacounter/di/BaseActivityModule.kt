package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.presentation.BaseActivity
import kztproject.jp.splacounter.auth.di.AuthModule
import kztproject.jp.splacounter.reward.di.RewardModule

@Module
internal abstract class BaseActivityModule {
    @ContributesAndroidInjector(modules = [RewardModule::class, AuthModule::class])
    internal abstract fun contributeBaseActivity(): BaseActivity
}
