package kztproject.jp.splacounter.viewmodel;

import android.support.annotation.StringRes;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.repository.AuthRepository;
import kztproject.jp.splacounter.R;

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
        if (inputString.length() == 0 ) {
            callback.showError(R.string.error_login_text_empty);
        } else {
            authRepository.login(inputString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> callback.showProgressDialog())
                    .doOnTerminate(() -> callback.dismissProgressDialog())
                    .subscribe(
                            () -> callback.loginSuccessed(),
                            e -> callback.loginFailed(e));
        }
    }

    public interface Callback {
        void showProgressDialog();

        void dismissProgressDialog();

        void loginSuccessed();

        void loginFailed(Throwable e);

        void showError(@StringRes int stringId);
    }
}
