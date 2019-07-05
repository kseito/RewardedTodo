package kztproject.jp.splacounter.auth.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.auth.ui.AuthViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module
interface AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: AuthViewModel): ViewModel
}