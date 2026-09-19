package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

/**
 * `POST /oauth/access_token` のレスポンス。
 *
 * リフレッシュを有効にしているため [expiresIn] は常に返る。
 * [refreshToken] は消費済みリフレッシュトークンを60秒以内に再送した場合に省略され、
 * そのとき [accessToken] は元の再発行分と同じ値が返る。
 */
data class TokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "expires_in") val expiresIn: Long?,
    @Json(name = "refresh_token") val refreshToken: String?,
    val scope: String?,
)
