package jp.kztproject.rewardedtodo.auth.api

import kotlinx.coroutines.Deferred
import jp.kztproject.rewardedtodo.auth.api.model.RewardUser
import retrofit2.http.*

interface RewardListLoginService {

    @FormUrlEncoded
    @POST("/api/auth/sign_up")
    fun createUser(@Field("todoist_id") todoistId: Long): Deferred<RewardUser>

    @GET("/api/auth/login")
    fun findUser(@Query("todoist_id") todoistId: Long): Deferred<RewardUser>
}