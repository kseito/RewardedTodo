package kztproject.jp.splacounter.reward.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import kztproject.jp.splacounter.reward.repository.RewardRepository

@Module
interface RewardRepositoryModule {

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}