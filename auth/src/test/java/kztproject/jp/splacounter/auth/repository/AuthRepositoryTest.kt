package kztproject.jp.splacounter.auth.repository


import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import kztproject.jp.splacounter.auth.DummyCreator
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.auth.api.model.UserResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import project.seito.screen_transition.preference.PrefsWrapper

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
        whenever(mockClient.getUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListLoginService.findUser(ArgumentMatchers.anyLong())).thenReturn(Single.just(dummyRewardUser))

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
        whenever(mockClient.getUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Single.just(dummyResponse))
        whenever(mockRewardListLoginService.createUser(ArgumentMatchers.anyLong())).thenReturn(Single.just(dummyRewardUser))

        repository.signUp("test")
                .test()
                .assertNoErrors()
                .assertComplete()

        assertThat(prefsWrapper.userId).isEqualTo(dummyRewardUser.id)
    }
}