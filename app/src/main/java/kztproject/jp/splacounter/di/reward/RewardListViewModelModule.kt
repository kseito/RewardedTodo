package kztproject.jp.splacounter.di.reward

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.reward.application.usecase.*
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

    @Binds
    fun bindGetRewardsUseCase(useCase: GetRewardsInteractor): GetRewardsUseCase

    @Binds
    fun bindGetPointUseCase(useCase: GetPointInteractor): GetPointUseCase
}