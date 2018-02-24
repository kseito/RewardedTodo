package kztproject.jp.splacounter.api;

import io.reactivex.Single;
import kztproject.jp.splacounter.model.Counter;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MiniatureGardenService {
    @GET("/main_pages/get_game_count")
    Single<Counter> getCounter(@Query("user_id") int userId);

    @PUT("/main_pages/consume_game_count")
    Single<Counter> cosumeCounter(@Query("user_id") int userId, @Query("point") int point);
}
