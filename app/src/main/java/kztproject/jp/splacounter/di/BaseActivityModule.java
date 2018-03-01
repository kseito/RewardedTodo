package kztproject.jp.splacounter.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import kztproject.jp.splacounter.activity.BaseActivity;

@Module
abstract class BaseActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract BaseActivity contributeBaseActivity();
}
