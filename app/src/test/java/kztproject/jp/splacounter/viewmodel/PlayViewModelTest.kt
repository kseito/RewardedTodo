package kztproject.jp.splacounter.viewmodel

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.DummyCreator
import kztproject.jp.splacounter.api.MiniatureGardenClient
import kztproject.jp.splacounter.preference.AppPrefsProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Created by k-seito on 2017/08/14.
 */
@RunWith(RobolectricTestRunner::class)
class PlayViewModelTest {

    val mockServiceClient = mock<MiniatureGardenClient>()

    val mockCallback = mock<PlayViewModel.Callback>()

    lateinit var appPrefsProvider: AppPrefsProvider

    lateinit var viewModel: PlayViewModel

    var dummyCounter = DummyCreator.createDummyCounter()

    @Before
    fun setup() {
        appPrefsProvider = AppPrefsProvider(RuntimeEnvironment.application)
        appPrefsProvider.get().putUserId(dummyCounter.id)

        viewModel = PlayViewModel(mockServiceClient, appPrefsProvider)
        viewModel.setCallback(mockCallback)
        val scheduler = Schedulers.trampoline()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun getGameCountSeccess() {
        whenever(mockServiceClient.getCounter(anyInt())).thenReturn(Observable.just(dummyCounter))

        viewModel.getGameCount()

        verify(mockCallback, times(1)).showProgressDialog()
        verify(mockCallback, times(1)).dismissProgressDialog()
        verify(mockCallback, times(1)).showGameCount(anyInt())
    }

    @Test
    fun consumeGameCountFailed() {
        val exception = IllegalArgumentException()
        whenever(mockServiceClient.consumeCounter(anyInt())).thenReturn(Observable.error(exception))

        viewModel.consumeGameCount()

        verify(mockCallback, times(1)).showError(exception)
    }
}