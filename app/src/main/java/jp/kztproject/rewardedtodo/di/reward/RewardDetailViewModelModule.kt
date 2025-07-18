package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import jp.kztproject.rewardedtodo.application.reward.usecase.*

@InstallIn(FragmentComponent::class)
@Module
interface RewardDetailViewModelModule {

    @Binds
    fun bindDeleteRewardUseCase(useCase: DeleteRewardInteractor): DeleteRewardUseCase

    @Binds
    fun bindGetRewardUseCase(useCase: GetRewardInteractor): GetRewardUseCase

    @Binds
    fun bindSaveRewardUseCase(useCase: SaveRewardInteractor): SaveRewardUseCase
}
