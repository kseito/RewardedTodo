package kztproject.jp.splacounter.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.mock.MockMyServiceClient;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

/**
 * Created by k-seito on 2016/01/24.
 */
@Module
public class MockPreferencesModule {
    public static final String POSTFIX = "_test";

    Context context;

    public MockPreferencesModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    MyServiceClient provideMyServiceClient() {
        return new MockMyServiceClient();
    }

    @Provides
    AppPrefsProvider provideAppPrefs() {
        return new AppPrefsProvider(context);
    }
}
