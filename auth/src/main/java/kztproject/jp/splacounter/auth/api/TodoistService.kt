package kztproject.jp.splacounter.auth.api

import kotlinx.coroutines.Deferred
import kztproject.jp.splacounter.auth.api.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TodoistService {
    @GET("/API/v7/sync")
    fun getUser(@Query("token") token: String, @Query("sync_token") syncToken: String,
                @Query("resource_types") resourceTypes: String): Deferred<UserResponse>
}
