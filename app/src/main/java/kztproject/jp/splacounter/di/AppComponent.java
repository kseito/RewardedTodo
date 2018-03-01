package kztproject.jp.splacounter.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import kztproject.jp.splacounter.MyApplication;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        BaseActivityModule.class,
        RewardActivityModule.class})
public interface AppComponent {
    @Component.Builder
    interface Builder{
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }

    void inject(MyApplication app);
}
