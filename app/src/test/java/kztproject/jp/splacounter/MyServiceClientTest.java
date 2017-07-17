package kztproject.jp.splacounter;

import org.junit.Before;
import org.junit.Test;

import kztproject.jp.splacounter.api.MyServiceClient;

/**
 * Created by k-seito on 2017/07/17.
 */

public class MyServiceClientTest {

    private MyServiceClient client;

    @Before
    public void setup() {
        client = new MyServiceClient();
    }

    @Test
    public void getUserFailed() {
        client.getUser("test");
    }

}
