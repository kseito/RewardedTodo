package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.application.reward.usecase.*

@Module
interface RewardDetailViewModelModule {

    @Binds
    fun bindDeleteRewardUseCase(useCase: DeleteRewardInteractor): DeleteRewardUseCase

    @Binds
    fun bindGetRewardUseCase(useCase: GetRewardInteractor): GetRewardUseCase

    @Binds
    fun bindSaveRewardUseCase(useCase: SaveRewardInteractor): SaveRewardUseCase
}