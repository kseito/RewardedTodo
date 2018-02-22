package kztproject.jp.splacounter.viewmodel;

import android.databinding.ObservableField;
import android.support.annotation.StringRes;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.repository.AuthRepository;
import kztproject.jp.splacounter.R;

public class AuthViewModel {

    private final AuthRepository authRepository;
    Callback callback;

    public ObservableField<String> inputString = new ObservableField<>();

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        inputString.set("");
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void login() {
        if (inputString.get().length() == 0 ) {
            callback.showError(R.string.error_login_text_empty);
        } else {
            authRepository.login(inputString.get())
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
