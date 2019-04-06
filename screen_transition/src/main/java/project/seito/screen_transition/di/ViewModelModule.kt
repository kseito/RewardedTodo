package project.seito.screen_transition.di

import android.arch.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: InjectedViewModelFactory): ViewModelProvider.Factory
}
