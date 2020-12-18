package kztproject.jp.splacounter.auth.repository


import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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