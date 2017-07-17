package kztproject.jp.splacounter.viewmodel;

import javax.inject.Inject;

import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

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
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        callback.showProgressDialog();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        callback.dismissProgressDialog();
                    }
                })
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {
                        callback.loginSuccessed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.loginFailed(e);
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        User user = userResponse.user;
                        AppPrefs schema = prefs.get();
                        schema.putUserId(user.id);
                        schema.putUserName(user.fullName);
                    }
                });
    }

    public interface Callback {
        void showProgressDialog();
        void dismissProgressDialog();
        void loginSuccessed();
        void loginFailed(Throwable e);
    }
}
