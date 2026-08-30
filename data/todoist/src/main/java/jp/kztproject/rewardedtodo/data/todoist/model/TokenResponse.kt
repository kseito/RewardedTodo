package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

/**
 * `POST /oauth/access_token` のレスポンス。
 *
 * リフレッシュを有効にしていないアプリでは [expiresIn] と [refreshToken] が返らない。
 * また消費済みリフレッシュトークンを60秒以内に再送した場合も [refreshToken] は省略される。
 */
data class TokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "expires_in") val expiresIn: Long?,
    @Json(name = "refresh_token") val refreshToken: String?,
    val scope: String?,
)
