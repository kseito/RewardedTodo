package jp.kztproject.rewardedtodo.data.auth

import jp.kztproject.rewardedtodo.data.auth.model.TodoistAuthentication
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TodoistApi {
    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun fetchAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String): TodoistAuthentication
}