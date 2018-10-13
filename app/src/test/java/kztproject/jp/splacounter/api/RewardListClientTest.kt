package kztproject.jp.splacounter.api

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class RewardListClientTest {

    private val mockWebServer: MockWebServer = MockWebServer()
    private val target = RewardListClient(mockWebServer.url(""))

    @Before
    fun setup() {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request == null || request.path == null) {
                    return MockResponse().setResponseCode(400)
                }
                if (request.path.matches(Regex("/api/users/[0-9]+"))) {
                    return MockResponse().setBody(readJsonFromResources("test_user.json")).setResponseCode(200)
                }
                if (request.method == HttpMethod.POST && request.path.matches(Regex("/api/users"))) {
                    return MockResponse().setBody(readJsonFromResources("new_user.json")).setResponseCode(200)
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
    fun createUser() {
        val actual = target.createUser(123456).blockingGet()
        assertThat(actual.id).isEqualTo(1)
        assertThat(actual.todoistId).isEqualTo(123456)
        assertThat(actual.point).isEqualTo(0)
    }

    @Test
    fun findUser() {
        val actual = target.findUser(1).blockingGet()
        assertThat(actual.id).isEqualTo(1)
        assertThat(actual.todoistId).isEqualTo(505)
        assertThat(actual.point).isEqualTo(24)
    }

    private fun readJsonFromResources(fileName: String): String {
        val inputStream = javaClass.classLoader.getResourceAsStream(fileName)
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
            fail(e.message, e)
        }

        return stringBuilder.toString()
    }
}