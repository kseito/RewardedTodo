package jp.kztproject.rewardedtodo.di.reward

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.data.reward.repository.RewardRepository

@InstallIn(SingletonComponent::class)
@Module
interface RewardRepositoryModule {

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}