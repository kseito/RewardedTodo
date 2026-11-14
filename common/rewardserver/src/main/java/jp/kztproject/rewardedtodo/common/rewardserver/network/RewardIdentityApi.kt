package jp.kztproject.rewardedtodo.common.rewardserver.network

import jp.kztproject.rewardedtodo.common.rewardserver.network.model.UserMeResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface RewardIdentityApi {
    @GET("api/users/me")
    suspend fun resolveUserId(@Header("Authorization") authorization: String? = null): UserMeResponse
}
