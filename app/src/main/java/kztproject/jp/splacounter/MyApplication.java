package kztproject.jp.splacounter;

import android.app.Application;

import com.facebook.stetho.Stetho;

import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.di.AppComponent;
import kztproject.jp.splacounter.di.AppModule;

/**
 * Created by k-seito on 2016/01/23.
 */
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
     }

    public AppComponent component() {
        return mAppComponent;
    }

    public void setmAppComponent(AppComponent component) {
        mAppComponent = component;
    }
}