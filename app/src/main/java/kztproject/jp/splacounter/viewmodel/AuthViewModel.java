package kztproject.jp.splacounter.viewmodel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

public class AuthViewModel {

    AppPrefsProvider prefs;
    MyServiceClient client;

    Callback callback;

    @Inject
    public AuthViewModel(MyServiceClient client, AppPrefsProvider prefs) {
        this.client = client;
        this.prefs = prefs;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void login(String inputString) {
        client.getUser(inputString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> callback.showProgressDialog())
                .doOnComplete(() -> callback.dismissProgressDialog())
                .subscribe(
                        userResponse -> {
                            User user = userResponse.user;
                            AppPrefs schema = prefs.get();
                            schema.putUserId(user.id);
                            schema.putUserName(user.fullName);
                        },
                        e -> callback.loginFailed(e),
                        () -> callback.loginSuccessed());
    }

    public interface Callback {
        void showProgressDialog();

        void dismissProgressDialog();

        void loginSuccessed();

        void loginFailed(Throwable e);
    }
}
