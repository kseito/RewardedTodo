package kztproject.jp.splacounter;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import kztproject.jp.splacounter.mock.MockMyServiceClient;
import kztproject.jp.splacounter.model.Counter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.spy;

/**
 * Created by k-seito on 2016/02/10.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyServiceTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class, true, false);
    private MockMyServiceClient myService;

    @Before
    public void setUp() {
        myService = spy(new MockMyServiceClient());
        Mockito.when(myService.getCounter()).thenReturn(
                Observable.just(MockMyServiceClient.generateCounter(10)));
        Mockito.when(myService.consumeCounter()).thenReturn(
                Observable.just(MockMyServiceClient.generateCounter(5)));

    }

    @Test
    public void getGameCount() {
        Observable<Counter> observable = myService.getCounter();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Counter>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("エラー:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Counter counter) {
                        Assert.assertEquals(10, counter.getCount());
                        System.out.println("カウント：" + counter.getCount());
                    }
                });
    }

    @Test
    public void consumeGameCount() {
        Observable<Counter> observable = myService.consumeCounter();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Counter>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("エラー:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Counter counter) {
                        Assert.assertEquals(5, counter.getCount());
                        System.out.println("カウント：" + counter.getCount());
                    }
                });
    }

}
