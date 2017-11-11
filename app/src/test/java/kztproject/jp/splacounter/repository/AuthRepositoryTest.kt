package kztproject.jp.splacounter.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.TodoistClient
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.AppPrefs
import kztproject.jp.splacounter.preference.AppPrefsProvider
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

    val mockAppPrefsProvider: AppPrefsProvider = AppPrefsProvider(RuntimeEnvironment.application)

    lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = AuthRepository(mockClient, mockAppPrefsProvider)
    }

    @Test
    fun login() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        whenever(mockClient.getUser(anyString())).thenReturn(Single.just(dummyResponse))

        repository.login("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        val appPrefs = AppPrefs.get(RuntimeEnvironment.application)
        assertEquals(appPrefs.userId, dummyResponse.user.id)
        assertEquals(appPrefs.userName, dummyResponse.user.fullName)
    }
}