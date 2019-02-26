package kztproject.jp.splacounter.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.reward.repository.IPointRepository
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import kztproject.jp.splacounter.reward.repository.PointRepository
import kztproject.jp.splacounter.reward.repository.RewardRepository

@Module
interface RepositoryModule {

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository

    @Binds
    fun bindPointRepository(repository: PointRepository): IPointRepository
}