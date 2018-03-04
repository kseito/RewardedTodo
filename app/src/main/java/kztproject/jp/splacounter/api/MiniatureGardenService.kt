package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.model.Counter
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface MiniatureGardenService {
    @GET("/main_pages/get_game_count")
    fun getCounter(@Query("user_id") userId: Int): Single<Counter>

    @PUT("/main_pages/consume_game_count")
    fun consumeCounter(@Query("user_id") userId: Int, @Query("point") point: Int): Single<Counter>
}
