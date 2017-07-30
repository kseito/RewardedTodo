package kztproject.jp.splacounter.api;

import javax.inject.Inject;

import io.reactivex.Single;
import kztproject.jp.splacounter.model.UserResponse;

public class TodoistClient {

    private final TodoistService service;

    @Inject
    public TodoistClient(TodoistService service) {
        this.service = service;
    }

    public Single<UserResponse> getUser(String token) {
        return service.getUser(token, "*", "[\"user\"]");
    }

}
