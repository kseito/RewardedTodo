package kztproject.jp.splacounter.reward.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.reward.presentation.detail.RewardDetailFragment
import kztproject.jp.splacounter.reward.presentation.list.RewardListFragment

@Module
abstract class RewardModule {

    @ContributesAndroidInjector(modules = [
        PointRepositoryModule::class,
        RewardListViewModelModule::class,
        RewardRepositoryModule::class,
        PointApiModule::class])
    internal abstract fun contributeRewardFragment(): RewardListFragment

    @ContributesAndroidInjector(modules = [
        RewardDetailViewModelModule::class,
        PointRepositoryModule::class,
        RewardRepositoryModule::class])
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}