package kztproject.jp.splacounter;

import android.app.Application;

import com.facebook.stetho.Stetho;

import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.di.AppComponent;
import kztproject.jp.splacounter.di.AppModule;
import kztproject.jp.splacounter.preference.PrefsWrapper;

public class MyApplication extends Application {

    public static final String PREF = "pref";

    @Singleton
    @Component(modules = AppModule.class)
    public interface AppAppComponent extends AppComponent {
    }

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mAppComponent == null) {
            mAppComponent = DaggerMyApplication_AppAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        PrefsWrapper.INSTANCE.initialize(getApplicationContext());
     }

    public AppComponent component() {
        return mAppComponent;
    }

    public void setmAppComponent(AppComponent component) {
        mAppComponent = component;
    }
}
