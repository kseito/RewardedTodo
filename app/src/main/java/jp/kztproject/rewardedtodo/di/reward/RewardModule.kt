package jp.kztproject.rewardedtodo.di.reward

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(includes = [PointRepositoryModule::class,
    RewardListViewModelModule::class,
    RewardRepositoryModule::class,
    PointApiModule::class,
    RewardDetailViewModelModule::class])
abstract class RewardModule