package kztproject.jp.splacounter.auth.api

import io.reactivex.Single
import kztproject.jp.splacounter.auth.api.model.RewardUser
import retrofit2.http.*

interface RewardListLoginService {

    @FormUrlEncoded
    @POST("/api/auth/sign_up")
    fun createUser(@Field("todoist_id") todoistId: Long): Single<RewardUser>

    @GET("/api/auth/login")
    fun findUser(@Query("todoist_id") todoistId: Long): Single<RewardUser>
}