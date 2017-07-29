package kztproject.jp.splacounter.viewmodel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.DummyCreator;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PlayViewModelTest {

    private MyServiceClient mockServiceClient;

    private AppPrefsProvider appPrefsProvider;

    private PlayViewModel viewModel;

    private PlayViewModel.Callback mockCallback;

    private Counter dummyCounter;

    @Before
    public void setup() {
        dummyCounter = DummyCreator.createDummyCounter();
        appPrefsProvider = new AppPrefsProvider(RuntimeEnvironment.application);
        appPrefsProvider.get().putUserId(dummyCounter.getId());

        mockServiceClient = mock(MyServiceClient.class);
        mockCallback = mock(PlayViewModel.Callback.class);
        viewModel = new PlayViewModel(mockServiceClient, appPrefsProvider);
        viewModel.setCallback(mockCallback);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void teardown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void getGameCountSuccess() {

        when(mockServiceClient.getCounter(anyInt())).thenReturn(Observable.just(dummyCounter));

        viewModel.getGameCount();

        verify(mockCallback, times(1)).showProgressDialog();
        verify(mockCallback, times(1)).dismissProgressDialog();
        verify(mockCallback, times(1)).showGameCount(anyInt());
    }

    @Test
    public void getGameCountFailed() {
        IllegalArgumentException exception = new IllegalArgumentException();
        when(mockServiceClient.getCounter(anyInt())).thenReturn(Observable.error(exception));

        viewModel.getGameCount();

        verify(mockCallback, times(1)).showError(exception);
    }

    @Test
    public void consumeGameCountSuccess() {

        when(mockServiceClient.consumeCounter(anyInt())).thenReturn(Observable.just(dummyCounter));

        viewModel.consumeGameCount();

        verify(mockCallback, times(1)).showProgressDialog();
        verify(mockCallback, times(1)).dismissProgressDialog();
        verify(mockCallback, times(1)).showGameCount(anyInt());
    }

    @Test
    public void consumeGameCountFailed() {

        IllegalArgumentException exception = new IllegalArgumentException();
        when(mockServiceClient.consumeCounter(anyInt())).thenReturn(Observable.error(exception));

        viewModel.consumeGameCount();

        verify(mockCallback, times(1)).showError(exception);
    }

}
