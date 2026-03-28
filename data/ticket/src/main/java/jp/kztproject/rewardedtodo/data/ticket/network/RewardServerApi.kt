package jp.kztproject.rewardedtodo.data.ticket.network

import jp.kztproject.rewardedtodo.data.ticket.network.model.ConsumePointRequest
import jp.kztproject.rewardedtodo.data.ticket.network.model.PointsInfoResponse
import jp.kztproject.rewardedtodo.data.ticket.network.model.UserMeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RewardServerApi {
    @GET("api/users/me")
    suspend fun resolveUserId(
        @Header("Authorization") authorization: String
    ): UserMeResponse

    @GET("api/points/{userId}")
    suspend fun getPoints(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String
    ): PointsInfoResponse

    @POST("api/points/{userId}/consume")
    suspend fun consumePoints(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
        @Body request: ConsumePointRequest
    ): Response<PointsInfoResponse>
}
