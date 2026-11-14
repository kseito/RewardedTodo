package jp.kztproject.rewardedtodo.di.ticket

import io.kotest.matchers.shouldBe
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import java.util.concurrent.TimeUnit

class RewardServerAuthenticatorTest {
    @Test
    fun `401 retries the original lottery POST once for the same user`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(401))
            server.enqueue(MockResponse().setResponseCode(401))
            val authorizedUsers = mutableListOf<Pair<String, String>>()
            val client = OkHttpClient.Builder()
                .authenticator(
                    RewardServerAuthenticator(server.url("/reward/")) { user, authorization ->
                        authorizedUsers += user to authorization
                        true
                    },
                )
                .build()

            client.newCall(lotteryRequest(server)).execute().use { it.code shouldBe 401 }

            authorizedUsers shouldBe listOf("user" to "Bearer token")
            server.requestCount shouldBe 2
            val original = checkNotNull(server.takeRequest(1, TimeUnit.SECONDS))
            val retry = checkNotNull(server.takeRequest(1, TimeUnit.SECONDS))
            retry.path shouldBe original.path
            retry.method shouldBe "POST"
            retry.body.readUtf8() shouldBe original.body.readUtf8()
        }
    }

    @Test
    fun `401 does not retry when the resolved user differs`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(401))
            val client = OkHttpClient.Builder()
                .authenticator(RewardServerAuthenticator(server.url("/reward/")) { _, _ -> false })
                .build()

            client.newCall(lotteryRequest(server)).execute().use { it.code shouldBe 401 }

            server.requestCount shouldBe 1
        }
    }

    private fun lotteryRequest(server: MockWebServer) = Request.Builder()
        .url(server.url("/reward/api/lotteries/user"))
        .header("Authorization", "Bearer token")
        .post("""{"request_id":"original-id","count":10}""".toRequestBody("application/json".toMediaType()))
        .build()
}
