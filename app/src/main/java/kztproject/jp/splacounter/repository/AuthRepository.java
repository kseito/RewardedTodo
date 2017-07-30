package kztproject.jp.splacounter.repository;

import javax.inject.Inject;

import io.reactivex.Completable;
import kztproject.jp.splacounter.api.TodoistClient;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

public class AuthRepository {

    private final TodoistClient client;
    private final AppPrefsProvider prefs;

    @Inject
    public AuthRepository(TodoistClient client, AppPrefsProvider prefs) {
        this.client = client;
        this.prefs = prefs;
    }

    public Completable login(String inputString) {
        return client.getUser(inputString)
                .flatMapCompletable(this::save);
    }

    private Completable save(UserResponse response) {
        User user = response.user;
        AppPrefs schema = prefs.get();
        schema.putUserId(user.id);
        schema.putUserName(user.fullName);
        return Completable.complete();
    }
}
