package kztproject.jp.splacounter.api

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.DummyCreator
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class TodoistClientTest {

    private val service: TodoistService = Mockito.mock(TodoistService::class.java)

    private lateinit var client: TodoistClient

    @Before
    fun setup() {
        client = TodoistClient(service)
    }

    @Test
    fun getUserSuccess() {
        val dummyUserResponse = DummyCreator.createDummyUserResponse()
        whenever(service.getUser(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(dummyUserResponse))

        client.getUser("test")
                .test()
                .assertNoErrors()
                .assertResult(dummyUserResponse)
                .assertComplete()
    }

}
