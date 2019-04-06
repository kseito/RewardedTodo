package kztproject.jp.splacounter.auth.ui

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.auth.repository.AuthRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import project.seito.auth.BuildConfig
import project.seito.auth.R
import java.lang.IllegalArgumentException

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class AuthViewModelTest {

    private val mockAuthRepository = mock<AuthRepository>()

    private val mockCallback = mock<AuthViewModel.Callback>()

    lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        viewModel = AuthViewModel(mockAuthRepository)
        viewModel.setCallback(mockCallback)

        val scheduler: Scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun signUpSuccess() {
        whenever(mockAuthRepository.signUp(anyString())).thenReturn(Completable.complete())

        viewModel.inputString.set("test")
        viewModel.signUp()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onSuccessSignUp()
    }

    @Test
    fun signUpFailed() {
        val exception = IllegalArgumentException()
        whenever(mockAuthRepository.signUp(anyString())).thenReturn(Completable.error(exception))

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
        whenever(mockAuthRepository.login(anyString())).thenReturn(Completable.complete())

        viewModel.inputString.set("test")
        viewModel.login()

        verify(mockCallback, times(1)).onStartAsyncProcess()
        verify(mockCallback, times(1)).onFinishAsyncProcess()
        verify(mockCallback, times(1)).onSuccessLogin()
    }

    @Test
    fun loginFailed() {
        val exception = NullPointerException()
        whenever(mockAuthRepository.login(anyString())).thenReturn(Completable.error(exception))

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