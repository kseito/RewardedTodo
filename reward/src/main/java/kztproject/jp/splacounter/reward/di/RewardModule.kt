package kztproject.jp.splacounter.reward.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.reward.detail.ui.RewardDetailFragment
import kztproject.jp.splacounter.reward.list.ui.RewardListFragment

@Module
abstract class RewardModule {

    @ContributesAndroidInjector(modules = [PointRepositoryModule::class, RewardRepositoryModule::class, PointApiModule::class, RewardDatabaseModule::class])
    internal abstract fun contributeRewardFragment(): RewardListFragment

    @ContributesAndroidInjector(modules = [PointRepositoryModule::class, RewardDatabaseModule::class, RewardRepositoryModule::class])
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}