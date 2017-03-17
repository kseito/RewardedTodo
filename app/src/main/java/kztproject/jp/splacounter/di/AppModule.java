package kztproject.jp.splacounter.di;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.api.MyServiceClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

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
}
