package kztproject.jp.splacounter.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;
import kztproject.jp.splacounter.view.fragment.RewardDetailFragment;
import kztproject.jp.splacounter.view.fragment.RewardFragment;

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract AuthFragment contributeAuthFragment();

    @ContributesAndroidInjector
    abstract PlayFragment contributePlayFragment();

    @ContributesAndroidInjector
    abstract RewardFragment contributeRewardFragment();

    @ContributesAndroidInjector
    abstract RewardDetailFragment contributeRewardAddFragment();
}
