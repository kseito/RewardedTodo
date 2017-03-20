package kztproject.jp.splacounter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.User;
import kztproject.jp.splacounter.model.UserResponse;
import kztproject.jp.splacounter.preference.AppPrefs;
import kztproject.jp.splacounter.preference.AppPrefsProvider;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by k-seito on 2017/03/18.
 */

public class AuthActivity extends AppCompatActivity{

    @Inject
    MyServiceClient client;

    @Inject
    AppPrefsProvider prefs;

    @Bind(R.id.token_text)
    EditText tokenText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).component().inject(this);
        if (prefs.get().getUserId() != 0) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    @OnClick(R.id.login_button)
    public void clickLogin(View view) {

        if (tokenText.length() == 0) {
            Toast.makeText(getApplicationContext(), "APIトークンを入力してください", Toast.LENGTH_SHORT).show();
        } else {
            login();
        }

    }

    private void login() {
        client.getUser(tokenText.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(AuthActivity.this);
                            progressDialog.setMessage("Now Loading...");
                        }
                        progressDialog.show();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.dismiss();
                    }
                })
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(), "ログインに成功しました", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        User user = userResponse.user;
                        AppPrefs schema = AppPrefs.get(getApplicationContext());
                        schema.putUserId(user.id);
                        schema.putUserName(user.fullName);
                    }
                });
    }
}
