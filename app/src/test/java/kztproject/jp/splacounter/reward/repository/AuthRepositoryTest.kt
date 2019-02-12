package kztproject.jp.splacounter.reward.repository

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.auth.api.model.UserResponse
import kztproject.jp.splacounter.auth.repository.AuthRepository
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
    private val mockRewardListLoginService = mock<RewardListLoginService>()
    private val prefsWrapper = PrefsWrapper(RuntimeEnvironment.application)

    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = AuthRepository(mockClient, mockRewardListLoginService, prefsWrapper)
    }

    @Test
    fun login() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(anyString(), anyString(), anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListLoginService.findUser(anyLong())).thenReturn(Single.just(dummyRewardUser))

        repository.login("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        assertThat(prefsWrapper.userId).isEqualTo(dummyRewardUser.id)
    }

    @Test
    fun signUp() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(anyString(), anyString(), anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListLoginService.createUser(anyLong())).thenReturn(Single.just(dummyRewardUser))

        repository.signUp("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        assertThat(prefsWrapper.userId).isEqualTo(dummyRewardUser.id)
    }
}