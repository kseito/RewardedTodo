package kztproject.jp.splacounter.viewmodel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.repository.AuthRepository;

@RunWith(RobolectricTestRunner.class)
public class AuthViewModelTest {

    AuthRepository authRepository = Mockito.mock(AuthRepository.class);

    AuthViewModel.Callback callbackMock = Mockito.mock(AuthViewModel.Callback.class);

    AuthViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new AuthViewModel(authRepository);
        viewModel.setCallback(callbackMock);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void teardown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void loginSuccess() {
        Mockito.when(authRepository.login(Mockito.anyString())).thenReturn(Completable.complete());

        viewModel.login("test");

        Mockito.verify(callbackMock, Mockito.times(1)).showProgressDialog();
        Mockito.verify(callbackMock, Mockito.times(1)).dismissProgressDialog();
        Mockito.verify(callbackMock, Mockito.times(1)).loginSuccessed();
    }

    @Test
    public void loginFailed() {
        NullPointerException exception = new NullPointerException();
        Mockito.when(authRepository.login(Mockito.anyString())).thenReturn(Completable.error(exception));

        viewModel.login("test");

        Mockito.verify(callbackMock, Mockito.times(1)).showProgressDialog();
        Mockito.verify(callbackMock, Mockito.times(1)).dismissProgressDialog();
        Mockito.verify(callbackMock, Mockito.times(1)).loginFailed(exception);
    }

    @Test
    public void loginWithEmptyText() {
        NullPointerException exception = new NullPointerException();
        Mockito.when(authRepository.login(Mockito.anyString())).thenReturn(Completable.error(exception));

        viewModel.login("");

        Mockito.verify(callbackMock, Mockito.times(1)).showError(R.string.error_login_text_empty);
    }

}
