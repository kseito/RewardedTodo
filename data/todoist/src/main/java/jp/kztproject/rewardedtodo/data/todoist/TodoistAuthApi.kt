package jp.kztproject.rewardedtodo.data.todoist

import jp.kztproject.rewardedtodo.data.todoist.model.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * TodoistのOAuthトークンエンドポイント。
 *
 * 本アプリはOAuth Client ID Metadata Documentで登録した公開クライアント
 * (`token_endpoint_auth_method: "none"`) のため、`client_secret` は送らない。
 * 代わりにPKCEの `code_verifier` でリクエストの出所を証明する。
 */
interface TodoistAuthApi {

    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun exchangeAuthorizationCode(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,
    ): TokenResponse

    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
    ): TokenResponse

    companion object {
        const val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"
    }
}
