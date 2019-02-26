package kztproject.jp.splacounter.auth.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.auth.ui.AuthFragment

@Module
abstract class AuthModule {

    @ContributesAndroidInjector(modules = [AuthRepositoryModule::class])
    internal abstract fun contributeAuthFragment(): AuthFragment
}