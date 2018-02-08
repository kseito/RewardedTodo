package kztproject.jp.splacounter.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.TodoistClient
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.PrefsWrapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    var mockClient = mock<TodoistClient>()

    lateinit var repository: AuthRepository

    @Before
    fun setup() {
        PrefsWrapper.initialize(RuntimeEnvironment.application)
        repository = AuthRepository(mockClient)
    }

    @Test
    fun login() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        whenever(mockClient.getUser(anyString())).thenReturn(Single.just(dummyResponse))

        repository.login("test")
                .   test()
                .assertNoErrors()
                .assertComplete()

        assertEquals(PrefsWrapper.userId, dummyResponse.user.id)
        assertEquals(PrefsWrapper.userName, dummyResponse.user.fullName)
    }
}