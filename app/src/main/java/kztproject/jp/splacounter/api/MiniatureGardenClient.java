package kztproject.jp.splacounter.api;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.util.GameCountUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MiniatureGardenClient {

    public static final String URL = "https://miniature-garden.herokuapp.com";

    private final MiniatureGardenService service;
    private final TodoistService todoistService;

    @Inject
    public MiniatureGardenClient(MiniatureGardenService service) {

        this.service = service;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TodoistService.URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        todoistService = retrofit.create(TodoistService.class);
    }

    public Single<UserResponse> getUser(String token) {
        return todoistService.getUser(token, "*", "[\"user\"]");
    }

    public Observable<Counter> getCounter(int userId) {
        return service.getCounter(userId);
    }

    public Observable<Counter> consumeCounter(int userId) {
        return service.cosumeCounter(userId, GameCountUtils.GAME_UNIT);
    }
}
