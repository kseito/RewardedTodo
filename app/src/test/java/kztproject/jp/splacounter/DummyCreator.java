package kztproject.jp.splacounter;

import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;

/**
 * Created by k-seito on 2017/07/19.
 */

public class DummyCreator {

    public static UserResponse createDummyUserResponse() {
        UserResponse response = new UserResponse();
        User user = new User();
        user.id = 1;
        user.fullName = "test_user";
        response.user = user;
        return response;
    }

}
