package jp.kztproject.rewardedtodo.data.reward.repository

import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.exception.RewardServerException
import retrofit2.HttpException
import com.squareup.moshi.Moshi

internal suspend fun <T> rewardRequest(block: suspend () -> T): T = try {
    block()
} catch (e: HttpException) {
    val code = runCatching {
        Moshi.Builder().build().adapter(
            Map::class.java,
        ).fromJson(e.response()?.errorBody()?.string().orEmpty())?.get("error") as? String
    }.getOrNull()
    if (code == "INSUFFICIENT_POINTS") throw LackOfTicketsException()
    throw RewardServerException(code ?: "HTTP_${e.code()}")
}
