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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.activity.MainActivity;
import kztproject.jp.splacounter.mock.MockMyServiceClient;
import kztproject.jp.splacounter.model.Counter;

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
        Mockito.when(myService.getCounter(1)).thenReturn(
                Observable.just(MockMyServiceClient.generateCounter(10)));
        Mockito.when(myService.consumeCounter(1)).thenReturn(
                Observable.just(MockMyServiceClient.generateCounter(5)));

    }

    @Test
    public void getGameCount() {
        Observable<Counter> observable = myService.getCounter(1);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        counter -> {
                            Assert.assertEquals(10, counter.getCount());
                            System.out.println("カウント：" + counter.getCount());
                        },
                        e -> System.out.println("エラー:" + e.getMessage()),
                        () -> System.out.println("Completed!")
                );
    }

    @Test
    public void consumeGameCount() {
        Observable<Counter> observable = myService.consumeCounter(1);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        counter -> {
                            Assert.assertEquals(5, counter.getCount());
                            System.out.println("カウント：" + counter.getCount());
                        },
                        e -> System.out.println("エラー:" + e.getMessage()),
                        () -> System.out.println("Completed!")
                );
    }

}
