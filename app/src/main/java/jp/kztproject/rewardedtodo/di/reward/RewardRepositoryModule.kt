package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.reward.application.repository.IRewardRepository
import jp.kztproject.rewardedtodo.reward.repository.RewardRepository

@Module
interface RewardRepositoryModule {

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}