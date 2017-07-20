package kztproject.jp.splacounter.viewmodel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.AuthRepository;

public class AuthViewModel {

    private final AuthRepository authRepository;
    Callback callback;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void login(String inputString) {
        authRepository.login(inputString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> callback.showProgressDialog())
                .doOnError(throwable -> callback.dismissProgressDialog())
                .doOnComplete(() -> callback.dismissProgressDialog())
                .subscribe(
                        () -> callback.loginSuccessed(),
                        e -> callback.loginFailed(e));
    }

    public interface Callback {
        void showProgressDialog();

        void dismissProgressDialog();

        void loginSuccessed();

        void loginFailed(Throwable e);
    }
}
