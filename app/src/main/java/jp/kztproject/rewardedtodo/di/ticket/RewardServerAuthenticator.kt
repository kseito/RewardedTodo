package jp.kztproject.rewardedtodo.di.ticket

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

internal class RewardServerAuthenticator(
    private val baseUrl: HttpUrl,
    private val refreshUserId: suspend (expectedUser: String, authorization: String) -> Boolean,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        val authorization = request.header("Authorization")
        val user = userId(request.url)
        if (request.tag(Retry::class.java) != null || authorization == null || user == null) return null

        val sameUser = try {
            runBlocking { refreshUserId(user, authorization) }
        } catch (e: HttpException) {
            if (e.code() != HTTP_UNAUTHORIZED) throw IOException("Reward user resolution failed", e)
            false
        }
        return if (sameUser) request.newBuilder().tag(Retry::class.java, Retry).build() else null
    }

    private fun userId(url: HttpUrl): String? {
        val sameOrigin = url.scheme == baseUrl.scheme && url.host == baseUrl.host && url.port == baseUrl.port
        if (!sameOrigin || !url.encodedPath.startsWith(baseUrl.encodedPath)) return null

        val segments = url.pathSegments.drop(baseUrl.pathSegments.size - 1)
        val isUserEndpoint = segments.firstOrNull() == "api" && segments.getOrNull(1) in userEndpoints
        return segments.getOrNull(2)?.takeIf { isUserEndpoint && it.isNotEmpty() }
    }

    private object Retry

    private companion object {
        val userEndpoints = setOf("rewards", "lotteries", "acquired-rewards", "points", "task-rewards")
    }
}
