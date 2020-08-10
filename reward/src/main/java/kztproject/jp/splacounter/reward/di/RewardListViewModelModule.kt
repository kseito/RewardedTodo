package kztproject.jp.splacounter.reward.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.reward.application.usecase.LotteryInteractor
import kztproject.jp.splacounter.reward.application.usecase.LotteryUseCase
import kztproject.jp.splacounter.reward.presentation.list.RewardListViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module
interface RewardListViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RewardListViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: RewardListViewModel): ViewModel

    @Binds
    fun bindLotteryUseCase(useCase: LotteryInteractor): LotteryUseCase
}