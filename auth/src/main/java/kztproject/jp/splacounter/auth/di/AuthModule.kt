package kztproject.jp.splacounter.auth.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.auth.ui.AuthFragment
import project.seito.screen_transition.di.FragmentScope

@Module
abstract class AuthModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [AuthRepositoryModule::class, AuthApiModule::class])
    internal abstract fun contributeAuthFragment(): AuthFragment
}