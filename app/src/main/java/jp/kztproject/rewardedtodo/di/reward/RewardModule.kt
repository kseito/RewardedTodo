package jp.kztproject.rewardedtodo.di.reward

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListFragment
import jp.kztproject.rewardedtodo.presentation.reward.detail.RewardDetailFragment

@Module(includes = [PointRepositoryModule::class,
    RewardListViewModelModule::class,
    RewardRepositoryModule::class,
    PointApiModule::class])
abstract class RewardModule {

//    @ContributesAndroidInjector(modules = [
//        PointRepositoryModule::class,
//        RewardListViewModelModule::class,
//        RewardRepositoryModule::class,
//        PointApiModule::class])
//    internal abstract fun contributeRewardFragment(): RewardListFragment

    @ContributesAndroidInjector(modules = [
        RewardDetailViewModelModule::class,
        PointRepositoryModule::class,
        RewardRepositoryModule::class])
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}