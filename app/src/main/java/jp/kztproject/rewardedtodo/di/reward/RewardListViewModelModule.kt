package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import jp.kztproject.rewardedtodo.application.reward.usecase.*

@InstallIn(FragmentComponent::class)
@Module
interface RewardListViewModelModule {

    @Binds
    fun bindLotteryUseCase(useCase: LotteryInteractor): LotteryUseCase

    @Binds
    fun bindGetRewardsUseCase(useCase: GetRewardsInteractor): GetRewardsUseCase

    @Binds
    fun bindGetPointUseCase(useCase: GetPointInteractor): GetPointUseCase
}
