package kztproject.jp.splacounter;

import javax.inject.Inject;

import io.reactivex.Completable;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

/**
 * Created by k-seito on 2017/07/19.
 */

public class UserRepository {

    private final MyServiceClient client;
    private final AppPrefsProvider prefs;

    @Inject
    public UserRepository(MyServiceClient client, AppPrefsProvider prefs) {
        this.client = client;
        this.prefs = prefs;
    }

    public Completable get(String inputString) {
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
