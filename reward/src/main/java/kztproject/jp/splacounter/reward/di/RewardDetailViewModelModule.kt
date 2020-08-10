package kztproject.jp.splacounter.reward.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module
interface RewardDetailViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RewardDetailViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: RewardDetailViewModel): ViewModel
}