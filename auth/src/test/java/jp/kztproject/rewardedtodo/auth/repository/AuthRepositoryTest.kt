package jp.kztproject.rewardedtodo.auth.repository


import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.auth.DummyCreator
import jp.kztproject.rewardedtodo.auth.api.RewardListLoginService
import jp.kztproject.rewardedtodo.auth.api.TodoistService
import jp.kztproject.rewardedtodo.auth.api.model.RewardUser
import jp.kztproject.rewardedtodo.auth.api.model.UserResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import project.seito.screen_transition.preference.PrefsWrapper

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    private val mockClient = mock<TodoistService>()
    private val mockRewardListLoginService = mock<RewardListLoginService>()
    private val prefsWrapper = PrefsWrapper(ApplicationProvider.getApplicationContext())

    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = AuthRepository(mockClient, mockRewardListLoginService, prefsWrapper)
    }

    @Test
    fun login() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(GlobalScope.async { dummyResponse })
        whenever(mockRewardListLoginService.findUser(ArgumentMatchers.anyLong())).thenReturn(GlobalScope.async { dummyRewardUser })

        runBlocking {
            repository.login("test")

            assertThat(prefsWrapper.userId).isEqualTo(dummyRewardUser.id)
        }
    }

    @Test
    fun signUp() {
        val dummyResponse: UserResponse = DummyCreator.createDummyUserResponse()
        val dummyRewardUser: RewardUser = DummyCreator.createDummyRewardUser()
        whenever(mockClient.getUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(GlobalScope.async { dummyResponse })
        whenever(mockRewardListLoginService.createUser(ArgumentMatchers.anyLong())).thenReturn(GlobalScope.async { dummyRewardUser })

        runBlocking {
            repository.signUp("test")

            assertThat(prefsWrapper.userId).isEqualTo(dummyRewardUser.id)
        }
    }
}