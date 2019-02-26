package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.reward.detail.ui.RewardDetailFragment
import kztproject.jp.splacounter.reward.list.ui.RewardFragment

@Module
internal abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    internal abstract fun contributeRewardFragment(): RewardFragment

    @ContributesAndroidInjector
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}
