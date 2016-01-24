package kztproject.jp.splacounter.module;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.MyApplication;

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
    SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences(MyApplication.PREF + POSTFIX, Context.MODE_PRIVATE);
    }
}
