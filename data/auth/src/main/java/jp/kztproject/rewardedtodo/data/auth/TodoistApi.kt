package jp.kztproject.rewardedtodo.data.auth

import retrofit2.http.Field
import retrofit2.http.POST

interface TodoistApi {
    @POST("oauth/access_token")
    fun fetchAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String)
}