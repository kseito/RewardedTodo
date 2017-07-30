package kztproject.jp.splacounter.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Observable;
import kztproject.jp.splacounter.DummyCreator;
import kztproject.jp.splacounter.model.Counter;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MiniatureGardenClientTest {

    @Mock
    private MiniatureGardenService mockService;

    @InjectMocks
    private MiniatureGardenClient client;

    @Before
    public void setup() {
    }

    @Test
    public void getCounterSuccess() {
        Counter counter = DummyCreator.createDummyCounter();
        when(mockService.getCounter(anyInt())).thenReturn(Observable.just(counter));

        client.getCounter(1)
                .test()
                .assertNoErrors()
                .assertResult(counter)
                .assertComplete();
    }

    @Test
    public void getCounterFailed() {
        IllegalArgumentException exception = new IllegalArgumentException();
        when(mockService.getCounter(anyInt())).thenReturn(Observable.error(exception));

        client.getCounter(1)
                .test()
                .assertError(exception);

    }

}
