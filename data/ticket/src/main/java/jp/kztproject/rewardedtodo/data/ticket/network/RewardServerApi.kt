package jp.kztproject.rewardedtodo.data.ticket.network

import jp.kztproject.rewardedtodo.data.ticket.network.model.ConsumePointRequest
import jp.kztproject.rewardedtodo.data.ticket.network.model.PointsInfoResponse
import jp.kztproject.rewardedtodo.data.ticket.network.model.UserMeResponse
import jp.kztproject.rewardedtodo.data.ticket.network.model.*
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RewardServerApi {
    @GET("api/task-rewards/{userId}")
    suspend fun getTaskRewards(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
    ): List<TaskRewardResponse>

    @PUT("api/task-rewards/{userId}/{taskId}")
    suspend fun updateTaskReward(
        @Path("userId") userId: String,
        @Path("taskId") taskId: String,
        @Header("Authorization") authorization: String,
        @Body request: UpdateTaskRewardRequest,
    ): TaskRewardResponse

    @POST("api/task-rewards/{userId}/import")
    suspend fun importTaskRewards(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
        @Body request: ImportTaskRewardsRequest,
    ): ImportTaskRewardsResponse

    @GET("api/users/me")
    suspend fun resolveUserId(@Header("Authorization") authorization: String): UserMeResponse

    @GET("api/points/{userId}")
    suspend fun getPoints(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
    ): PointsInfoResponse

    @POST("api/points/{userId}/consume")
    suspend fun consumePoints(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
        @Body request: ConsumePointRequest,
    ): PointsInfoResponse
}
