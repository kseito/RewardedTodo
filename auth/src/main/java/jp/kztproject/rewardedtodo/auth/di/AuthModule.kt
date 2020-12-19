package jp.kztproject.rewardedtodo.auth.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.kztproject.rewardedtodo.auth.ui.AuthFragment
import project.seito.screen_transition.di.FragmentScope

@Module
abstract class AuthModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [AuthViewModelModule::class, AuthRepositoryModule::class, AuthApiModule::class])
    internal abstract fun contributeAuthFragment(): AuthFragment
}