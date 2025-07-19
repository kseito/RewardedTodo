package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardInteractor
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase

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
