package kztproject.jp.splacounter;

import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;

/**
 * Created by k-seito on 2017/07/19.
 */

public class DummyCreator {

    public static UserResponse createDummyUserResponse() {
        UserResponse response = new UserResponse();
        User user = new User();
        user.setId(1);
        user.setFullName("test_user");
        response.setUser(user);
        return response;
    }

    public static Counter createDummyCounter() {
        Counter counter = new Counter();
        counter.setId(1);
        counter.setCount(10);
        return counter;
    }
}
