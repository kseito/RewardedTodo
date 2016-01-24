package kztproject.jp.splacounter;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.component.PrefsComponent;
import kztproject.jp.splacounter.module.PreferencesModule;

/**
 * Created by k-seito on 2016/01/23.
 */
public class MyApplication extends Application {

    public static final String PREF = "pref";

    @Singleton
    @Component(modules = PreferencesModule.class)
    public interface AppPrefsComponent extends PrefsComponent {
    }

    private PrefsComponent mPrefsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mPrefsComponent == null) {
            mPrefsComponent = DaggerMyApplication_AppPrefsComponent.builder()
                    .preferencesModule(new PreferencesModule(this))
                    .build();
        }
     }

    public PrefsComponent component() {
        return mPrefsComponent;
    }

    public void setmPrefsComponent(PrefsComponent component) {
        mPrefsComponent = component;
    }
}
