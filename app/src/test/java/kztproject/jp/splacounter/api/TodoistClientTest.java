package kztproject.jp.splacounter.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Single;
import kztproject.jp.splacounter.DummyCreator;
import kztproject.jp.splacounter.model.UserResponse;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TodoistClientTest {

    @Mock
    private TodoistService service;

    @InjectMocks
    private TodoistClient client;

    @Test
    public void getUserSuccess() {
        UserResponse dummyUserResponse = DummyCreator.createDummyUserResponse();
        when(service.getUser(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(dummyUserResponse));

        client.getUser("test")
                .test()
                .assertNoErrors()
                .assertResult(dummyUserResponse)
                .assertComplete();
    }

}
