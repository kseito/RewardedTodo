package kztproject.jp.splacounter.di.reward

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.repository.RewardRepository

@Module
interface RewardRepositoryModule {

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}