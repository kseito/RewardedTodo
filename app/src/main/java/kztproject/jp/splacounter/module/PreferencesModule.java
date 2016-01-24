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
public class PreferencesModule {

    Context context;

    public PreferencesModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    SharedPreferences providePreferences() {
        return context.getSharedPreferences(MyApplication.PREF, Context.MODE_PRIVATE);
    }
}
