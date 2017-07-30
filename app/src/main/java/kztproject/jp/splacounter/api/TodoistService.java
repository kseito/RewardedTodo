package kztproject.jp.splacounter.api;

import io.reactivex.Single;
import kztproject.jp.splacounter.model.UserResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TodoistService {

    @GET("/API/v7/sync")
    Single<UserResponse> getUser(@Query("token") String token, @Query("sync_token") String syncToken,
                                 @Query("resource_types") String resourceTypes);

}
