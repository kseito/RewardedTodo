package jp.kztproject.rewardedtodo.data.reward.network

import retrofit2.http.*

interface RewardApi {
    @GET("api/rewards/{user}")
    suspend fun list(@Path("user") user: String): List<RewardResponse>

    @POST("api/rewards/{user}")
    suspend fun create(@Path("user") user: String, @Body request: SaveRewardRequest): RewardResponse

    @PUT("api/rewards/{user}/{id}")
    suspend fun update(
        @Path("user") user: String,
        @Path("id") id: Int,
        @Body request: SaveRewardRequest,
    ): RewardResponse

    @DELETE("api/rewards/{user}/{id}")
    suspend fun delete(@Path("user") user: String, @Path("id") id: Int, @Query("version") version: Int)

    @POST("api/lotteries/{user}")
    suspend fun draw(@Path("user") user: String, @Body request: LotteryRequest): LotteryResponse

    @GET("api/acquired-rewards/{user}")
    suspend fun inventory(
        @Path("user") user: String,
        @Query("status") status: String,
        @Query("cursor") cursor: String?,
    ): AcquiredRewardPageResponse

    @PUT("api/acquired-rewards/{user}/{id}/use")
    suspend fun use(@Path("user") user: String, @Path("id") id: String): AcquiredRewardResponse
}
