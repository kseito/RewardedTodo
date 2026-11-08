package jp.kztproject.rewardedtodo.di.reward

import jp.kztproject.rewardedtodo.application.reward.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase

@InstallIn(ViewModelComponent::class)
@Module
interface RewardListViewModelModule {
    @Binds fun bindRecoverLottery(interactor: RecoverLotteryInteractor): RecoverLotteryUseCase

    @Binds fun bindGetAcquiredRewards(interactor: GetAcquiredRewardsInteractor): GetAcquiredRewardsUseCase

    @Binds fun bindUseAcquiredReward(interactor: UseAcquiredRewardInteractor): UseAcquiredRewardUseCase

    @Binds
    fun bindLotteryUseCase(useCase: LotteryInteractor): LotteryUseCase

    @Binds
    fun bindBatchLotteryUseCase(useCase: BatchLotteryInteractor): BatchLotteryUseCase

    @Binds
    fun bindGetRewardsUseCase(useCase: GetRewardsInteractor): GetRewardsUseCase

    @Binds
    fun bindGetPointUseCase(useCase: GetPointInteractor): GetPointUseCase
}
