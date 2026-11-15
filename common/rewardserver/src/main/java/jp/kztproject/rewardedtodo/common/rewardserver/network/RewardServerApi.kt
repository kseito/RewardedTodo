package jp.kztproject.rewardedtodo.common.rewardserver.network

import jp.kztproject.rewardedtodo.common.rewardserver.network.model.PointsResponse
import jp.kztproject.rewardedtodo.common.rewardserver.network.model.*
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface RewardServerApi {
    @GET("api/task-rewards/{userId}")
    suspend fun getTaskRewards(@Path("userId") userId: String): List<TaskRewardResponse>

    @PUT("api/task-rewards/{userId}/{taskId}")
    suspend fun updateTaskReward(
        @Path("userId") userId: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskRewardRequest,
    ): TaskRewardResponse

    @GET("api/points/{userId}")
    suspend fun getPoints(@Path("userId") userId: String): PointsResponse
}
