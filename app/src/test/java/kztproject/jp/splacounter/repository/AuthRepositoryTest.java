package kztproject.jp.splacounter.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import io.reactivex.Single;
import kztproject.jp.splacounter.DummyCreator;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AuthRepositoryTest {

    private MyServiceClient mockClient = mock(MyServiceClient.class);

    private AppPrefsProvider mockAppPrefsProvider = new AppPrefsProvider(RuntimeEnvironment.application);

    private AuthRepository repository;

    @Before
    public void setup() {
        repository = new AuthRepository(mockClient, mockAppPrefsProvider);
    }

    @Test
    public void login() {
        UserResponse dummyResponse = DummyCreator.createDummyUserResponse();
        when(mockClient.getUser(anyString()))
                .thenReturn(Single.just(dummyResponse));

        repository.login("test")
                .test()
                .assertNoErrors()
                .assertComplete();

        AppPrefs appPrefs = AppPrefs.get(RuntimeEnvironment.application);
        assertThat(appPrefs.getUserId(), is(dummyResponse.user.id));
        assertThat(appPrefs.getUserName(),is(dummyResponse.user.fullName));
    }
}
