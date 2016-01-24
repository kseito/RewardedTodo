package kztproject.jp.splacounter.component;

import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.MainActivity;
import kztproject.jp.splacounter.module.PreferencesModule;

/**
 * Created by k-seito on 2016/01/24.
 */
@Singleton
@Component(modules = PreferencesModule.class)
public interface PrefsComponent {
    void inject(MainActivity mainActivity);
}
