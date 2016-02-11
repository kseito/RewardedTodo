package kztproject.jp.splacounter.api;

import kztproject.jp.splacounter.model.Counter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by k-seito on 2016/02/07.
 */
public class MyServiceClient {

    private static final String URL = "https://miniature-garden.herokuapp.com";

    private final MyService service;

    public MyServiceClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MyService.class);
    }

    public Observable<Counter> getCounter() {
        return service.getCounter();
    }

    public Observable<Counter> consumeCounter() {
        return service.cosumeCounter();
    }

    public interface MyService {
        @GET("/main_pages/get_game_count")
        Observable<Counter> getCounter();

        @PUT("/main_pages/consume_game_count")
        Observable<Counter> cosumeCounter();
    }

}
