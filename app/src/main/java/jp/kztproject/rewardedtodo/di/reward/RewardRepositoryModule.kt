package jp.kztproject.rewardedtodo.di.reward

import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import jp.kztproject.rewardedtodo.domain.reward.repository.IAcquiredRewardRepository
import jp.kztproject.rewardedtodo.data.reward.repository.LotteryRepository
import jp.kztproject.rewardedtodo.data.reward.repository.AcquiredRewardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import jp.kztproject.rewardedtodo.data.reward.repository.RewardRepository

@InstallIn(SingletonComponent::class)
@Module
interface RewardRepositoryModule {
    @Binds fun bindLotteryRepository(repository: LotteryRepository): ILotteryRepository

    @Binds fun bindAcquiredRewardRepository(repository: AcquiredRewardRepository): IAcquiredRewardRepository

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}
