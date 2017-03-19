package kztproject.jp.splacounter.mock;

import javax.inject.Inject;

import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.Counter;
import rx.Observable;

/**
 * Created by k-seito on 2016/02/10.
 */
public class MockMyServiceClient extends MyServiceClient {

    private static final int DUMMY_ID = 123;
    private static final String DYMMY_DATE = "2016/01/01 00:00:00";

    @Override
    public Observable<Counter> getCounter(int userId) {
        return Observable.just(generateCounter(0));
    }

    @Override
    public Observable<Counter> consumeCounter(int userId) {
        return Observable.just(generateCounter(0));
    }

    public static Counter generateCounter(int count) {
        Counter counter = new Counter();
        counter.setId(DUMMY_ID);
        counter.setCount(count);
        counter.setCreatedAt(DYMMY_DATE);
        counter.setUpdatedAt(DYMMY_DATE);
        return counter;
    }
}
