package kztproject.jp.splacounter.api;

import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.model.UserResponse;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import rx.Observable;
import rx.internal.operators.OperatorSerialize;

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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myService = retrofit.create(MyService.class);

        retrofit = new Retrofit.Builder()
                .baseUrl(TodoistService.URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        todoistService = retrofit.create(TodoistService.class);
    }

    public Observable<UserResponse> getUser(String token) {
        return todoistService.getUser(token, "*", "[\"user\"]");
    }

    public Observable<Counter> getCounter() {
        return myService.getCounter();
    }

    public Observable<Counter> consumeCounter() {
        return myService.cosumeCounter();
    }

    public interface MyService {
        @GET("/main_pages/get_game_count")
        Observable<Counter> getCounter();

        @PUT("/main_pages/consume_game_count")
        Observable<Counter> cosumeCounter();
    }
}
