package jp.kztproject.rewardedtodo.di.reward

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import jp.kztproject.rewardedtodo.application.reward.GetRewardsInteractor
import jp.kztproject.rewardedtodo.application.reward.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.LotteryInteractor
import jp.kztproject.rewardedtodo.application.reward.LotteryUseCase
import jp.kztproject.rewardedtodo.reward.application.usecase.*
import jp.kztproject.rewardedtodo.reward.presentation.list.RewardListViewModel
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