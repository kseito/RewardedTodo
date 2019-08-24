package kztproject.jp.splacounter.auth.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.auth.MainCoroutineRule
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import project.seito.auth.R

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private val mockAuthRepository = mock<IAuthRepository>()

    private val mockCallback = mock<AuthViewModel.Callback>()

    private lateinit var viewModel: AuthViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = AuthViewModel(mockAuthRepository)
        viewModel.setCallback(mockCallback)
    }

    @Test
    fun signUpSuccess() {
        mainCoroutineRule.pauseDispatcher()

        viewModel.inputString.set("test")
        viewModel.signUp()

        assertThat(viewModel.dataLoading.value).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.dataLoading.value).isFalse()

        verify(mockCallback, times(1)).onSuccessSignUp()
    }

    @Test
    fun signUpFailed() {
        runBlocking {
            whenever(mockAuthRepository.signUp(anyString())).thenAnswer { throw IllegalArgumentException() }
        }

        mainCoroutineRule.pauseDispatcher()

        viewModel.inputString.set("test")
        viewModel.signUp()

        assertThat(viewModel.dataLoading.value).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.dataLoading.value).isFalse()

        verify(mockCallback, times(1)).onFailedSignUp()
    }

    @Test
    fun signUpWithEmptyText() {
        viewModel.signUp()

        verify(mockCallback, times(1)).onError(R.string.error_login_text_empty)
    }

    @Test
    fun loginSuccess() {
        mainCoroutineRule.pauseDispatcher()

        viewModel.inputString.set("test")
        viewModel.login()

        assertThat(viewModel.dataLoading.value).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.dataLoading.value).isFalse()

        verify(mockCallback, times(1)).onSuccessLogin()
    }

    @Test
    fun loginFailed() {
        val exception = NullPointerException()
        runBlocking {
            whenever(mockAuthRepository.login(anyString())).thenAnswer { throw exception }
        }

        mainCoroutineRule.pauseDispatcher()

        viewModel.inputString.set("test")
        viewModel.login()

        assertThat(viewModel.dataLoading.value).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.dataLoading.value).isFalse()

        verify(mockCallback, times(1)).onFailedLogin(exception)
    }

    @Test
    fun loginWithEmptyText() {
        viewModel.login()

        verify(mockCallback, times(1)).onError(R.string.error_login_text_empty)
    }
}