package kztproject.jp.splacounter.mock;

import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.Counter;
import rx.Observable;

/**
 * Created by k-seito on 2016/02/10.
 */
public class MockMyService implements MyServiceClient.MyService {

    @Override
    public Observable<Counter> getCounter() {
        return null;
    }

    @Override
    public Observable<Counter> cosumeCounter() {
        return null;
    }
}
