package kztproject.jp.splacounter.api;

import io.reactivex.Observable;
import kztproject.jp.splacounter.model.UserResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by k-seito on 2017/03/18.
 */

public interface TodoistService {

    static final String URL = "https://todoist.com";

    @GET("/API/v7/sync")
    Observable<UserResponse> getUser(@Query("token") String token, @Query("sync_token") String syncToken,
                                     @Query("resource_types") String resourceTypes);

}
