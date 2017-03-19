package kztproject.jp.splacounter.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

/**
 * Created by k-seito on 2016/01/24.
 */
@Module
public class AppModule {

    Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    MyServiceClient provideMyServiceClient() {
        return new MyServiceClient();
    }

    @Provides
    AppPrefsProvider provideAppPrefs() {
        return new AppPrefsProvider(context);
    }
}
