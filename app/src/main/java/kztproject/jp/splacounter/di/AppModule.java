package kztproject.jp.splacounter.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kztproject.jp.splacounter.BuildConfig;
import kztproject.jp.splacounter.api.RewardListClient;
import kztproject.jp.splacounter.api.TodoistService;
import kztproject.jp.splacounter.database.AppDatabase;
import kztproject.jp.splacounter.database.RewardDao;
import kztproject.jp.splacounter.exception.InvalidUrlException;
import okhttp3.HttpUrl;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class AppModule {

    @Provides
    @Singleton
    TodoistService provideTodoistService() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.TODOIST_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoistService.class);
    }

    @Provides
    @Singleton
    RewardListClient provideRewardListClient() {
        HttpUrl url = HttpUrl.parse(BuildConfig.REWARD_LIST_SERVER_URL);
        if (url != null) {
            return new RewardListClient(url);
        } else {
            throw new InvalidUrlException();
        }
    }

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "splacounter")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    RewardDao providesRewardDao(AppDatabase appDatabase) {
        return appDatabase.rewardDao();
    }
}
