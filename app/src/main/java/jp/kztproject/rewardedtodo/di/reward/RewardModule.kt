package jp.kztproject.rewardedtodo.di.reward

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.kztproject.rewardedtodo.reward.presentation.detail.RewardDetailFragment
import jp.kztproject.rewardedtodo.reward.presentation.list.RewardListFragment

@Module
abstract class RewardModule {

    @ContributesAndroidInjector(modules = [
        PointRepositoryModule::class,
        jp.kztproject.rewardedtodo.di.reward.RewardListViewModelModule::class,
        RewardRepositoryModule::class,
        PointApiModule::class])
    internal abstract fun contributeRewardFragment(): RewardListFragment

    @ContributesAndroidInjector(modules = [
        jp.kztproject.rewardedtodo.di.reward.RewardDetailViewModelModule::class,
        PointRepositoryModule::class,
        RewardRepositoryModule::class])
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}