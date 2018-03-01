package kztproject.jp.splacounter.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import kztproject.jp.splacounter.activity.RewardActivity;

@Module
abstract class RewardActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract RewardActivity contributeRewardActivity();
}
