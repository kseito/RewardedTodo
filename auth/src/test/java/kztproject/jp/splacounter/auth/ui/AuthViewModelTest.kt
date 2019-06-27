package kztproject.jp.splacounter.auth.ui

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import project.seito.auth.R

@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    private val mockAuthRepository = mock<IAuthRepository>()

    private val mockCallback = mock<AuthViewModel.Callback>()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        viewModel = AuthViewModel(mockAuthRepository)
        viewModel.setCallback(mockCallback)
    }

    @Test
    fun signUpSuccess() {
        viewModel.inputString.set("test")
        viewModel.signUp()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onSuccessSignUp()
    }

    @Test
    fun signUpFailed() {
        runBlocking {
            whenever(mockAuthRepository.signUp(anyString())).thenAnswer { throw IllegalArgumentException() }
        }

        viewModel.inputString.set("test")
        viewModel.signUp()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onFailedSignUp()
    }

    @Test
    fun signUpWithEmptyText() {
        viewModel.signUp()

        verify(mockCallback, times(1)).onError(R.string.error_login_text_empty)
    }

    @Test
    fun loginSuccess() {
        viewModel.inputString.set("test")
        viewModel.login()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onSuccessLogin()
    }

    @Test
    fun loginFailed() {
        val exception = NullPointerException()
        runBlocking {
            whenever(mockAuthRepository.login(anyString())).thenAnswer { throw exception }
        }

        viewModel.inputString.set("test")
        viewModel.login()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onFailedLogin(exception)
    }

    @Test
    fun loginWithEmptyText() {
        viewModel.login()

        verify(mockCallback, times(1)).onError(R.string.error_login_text_empty)
    }
}