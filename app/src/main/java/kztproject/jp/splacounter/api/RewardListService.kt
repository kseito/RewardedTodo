package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.database.model.RewardPoint
import kztproject.jp.splacounter.model.RewardUser
import retrofit2.http.*

interface RewardListService {

    @FormUrlEncoded
    @POST("/api/users")
    fun createUser(@Field("todoist_id") todoistId: Long): Single<RewardUser>

    @GET("/api/users/{todoist_id}")
    fun findUser(@Path("todoist_id") todoistId: Long): Single<RewardUser>

    @GET("/api/users/{user_id}")
    fun getPoint(@Path("user_id") userId: Long): Single<RewardPoint>

}