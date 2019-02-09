package kztproject.jp.splacounter.di

import dagger.Binds
import dagger.Module
import kztproject.jp.splacounter.auth.repository.AuthRepository
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import kztproject.jp.splacounter.reward.repository.RewardRepository

@Module
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepository): IAuthRepository

    @Binds
    fun bindRewardRepository(repository: RewardRepository): IRewardRepository
}