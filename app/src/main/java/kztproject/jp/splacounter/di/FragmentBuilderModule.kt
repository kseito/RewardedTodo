package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.view.fragment.AuthFragment
import kztproject.jp.splacounter.view.fragment.RewardDetailFragment
import kztproject.jp.splacounter.view.fragment.RewardFragment

@Module
internal abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    internal abstract fun contributeAuthFragment(): AuthFragment

    @ContributesAndroidInjector
    internal abstract fun contributeRewardFragment(): RewardFragment

    @ContributesAndroidInjector
    internal abstract fun contributeRewardAddFragment(): RewardDetailFragment
}
