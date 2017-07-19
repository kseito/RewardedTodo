package kztproject.jp.splacounter.api;

import io.reactivex.Observable;
import io.reactivex.Single;
import kztproject.jp.splacounter.domain.GameCountUtils;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.model.UserResponse;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by k-seito on 2016/02/07.
 */
public class MyServiceClient {

    private static final String URL = "https://miniature-garden.herokuapp.com";

    private final MyService myService;
    private final TodoistService todoistService;

    public MyServiceClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myService = retrofit.create(MyService.class);

        retrofit = new Retrofit.Builder()
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
        return myService.getCounter(userId);
    }

    public Observable<Counter> consumeCounter(int userId) {
        return myService.cosumeCounter(userId, GameCountUtils.GAME_UNIT);
    }

    public interface MyService {
        @GET("/main_pages/get_game_count")
        Observable<Counter> getCounter(@Query("user_id") int userId);

        @PUT("/main_pages/consume_game_count")
        Observable<Counter> cosumeCounter(@Query("user_id") int userId, @Query("point") int point);
    }
}
