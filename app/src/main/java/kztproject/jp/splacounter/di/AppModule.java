package kztproject.jp.splacounter.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.api.MiniatureGardenClient;
import kztproject.jp.splacounter.api.MiniatureGardenService;
import kztproject.jp.splacounter.preference.AppPrefsProvider;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by k-seito on 2016/01/24.
 */
@Module
public class AppModule {

    Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    AppPrefsProvider provideAppPrefs() {
        return new AppPrefsProvider(context);
    }

    @Provides
    MiniatureGardenService provideMiniatureGardenService() {
        return new Retrofit.Builder()
                .baseUrl(MiniatureGardenClient.URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MiniatureGardenService.class);
    }
}
