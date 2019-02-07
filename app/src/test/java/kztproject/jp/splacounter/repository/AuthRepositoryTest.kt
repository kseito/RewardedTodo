package kztproject.jp.splacounter.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.RewardListClient
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.auth.repository.AuthRepository
import kztproject.jp.splacounter.model.RewardUser
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.PrefsWrapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    private val mockClient = mock<TodoistService>()
    private val mockRewardListClient = mock<RewardListClient>()

    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        PrefsWrapper.initialize(RuntimeEnvironment.application)
        repository = AuthRepository(mockClient, mockRewardListClient)
    }

    @Test
    fun login() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(anyString(), anyString(), anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListClient.findUser(anyLong())).thenReturn(Single.just(dummyRewardUser))

        repository.login("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        assertThat(PrefsWrapper.userId).isEqualTo(dummyRewardUser.id)
    }

    @Test
    fun signUp() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(anyString(), anyString(), anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListClient.createUser(anyLong())).thenReturn(Single.just(dummyRewardUser))

        repository.signUp("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        assertThat(PrefsWrapper.userId).isEqualTo(dummyRewardUser.id)
    }
}