package jp.kztproject.rewardedtodo.reward.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.reward.infrastructure.api.RewardPointService
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import project.seito.screen_transition.api.HttpMethod
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class RewardPointServiceTest {

    private val mockWebServer: MockWebServer = MockWebServer()
    private val target = Retrofit.Builder()
            .baseUrl(mockWebServer.url(""))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RewardPointService::class.java)

    @Before
    fun setup() {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request == null || request.path == null) {
                    return MockResponse().setResponseCode(400)
                }
                if (request.method == HttpMethod.GET && request.path.matches(Regex("/api/users/[0-9]+"))) {
                    return MockResponse().setBody(readJsonFromResources("get_point.json")).setResponseCode(200)
                }
                if (request.method == HttpMethod.PUT && request.path.matches(Regex("/api/users/[0-9]+"))) {
                    return MockResponse().setBody(readJsonFromResources("test_user.json")).setResponseCode(200)
                }
                return MockResponse().setResponseCode(404)
            }
        }
        mockWebServer.setDispatcher(dispatcher)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getPoint() {
        runBlocking {
            val actual = target.getPoint(1).await()
            assertThat(actual.value).isEqualTo(12)
        }
    }

    @Test
    fun updatePoint() {
        runBlocking {
            val actual = target.updatePoint(1, 1).await()
            assertThat(actual.id).isEqualTo(1)
            assertThat(actual.todoistId).isEqualTo(505)
            assertThat(actual.point).isEqualTo(24)
        }
    }

    private fun readJsonFromResources(fileName: String): String {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        try {
            var buffer: String?
            while (true) {
                buffer = bufferedReader.readLine()
                if (buffer == null) {
                    break
                }
                stringBuilder.append(buffer)
            }
        } catch (e: IOException) {
            fail<Any>(e.message, e)
        }

        return stringBuilder.toString()
    }
}