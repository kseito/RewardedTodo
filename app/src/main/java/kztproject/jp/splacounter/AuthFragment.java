package kztproject.jp.splacounter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import kztproject.jp.splacounter.activity.MainActivity;
import kztproject.jp.splacounter.viewmodel.AuthViewModel;

/**
 * Created by k-seito on 2017/07/17.
 */

public class AuthFragment extends Fragment implements AuthViewModel.Callback {

    @Bind(R.id.token_text)
    EditText tokenText;

    private ProgressDialog progressDialog;

    @Inject
    AuthViewModel viewModel;

    public static AuthFragment newInstance() {
        Bundle args = new Bundle();
        AuthFragment fragment = new AuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_auth, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).component().inject(this);
        viewModel.setCallback(this);
    }

    @OnClick(R.id.login_button)
    public void clickLogin(View view) {
        if (tokenText.length() == 0) {
            Toast.makeText(getContext(), "APIトークンを入力してください", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.login(tokenText.getText().toString());
        }

    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Now Loading...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void loginSuccessed() {
        Toast.makeText(getActivity(), "ログインに成功しました", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));

        //TODO call replace fragment
    }

    @Override
    public void loginFailed(Throwable e) {
        Toast.makeText(getActivity(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
    }
}
