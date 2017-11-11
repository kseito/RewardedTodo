package kztproject.jp.splacounter.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.viewmodel.AuthViewModel;

public class AuthFragment extends Fragment implements AuthViewModel.Callback {

    @BindView(R.id.token_text)
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
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).component().inject(this);
        viewModel.setCallback(this);
    }

    @OnClick(R.id.login_button)
    public void clickLogin(View view) {
        viewModel.login(tokenText.getText().toString());
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

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlayFragment.newInstance())
                .commit();
    }

    @Override
    public void loginFailed(Throwable e) {
        Toast.makeText(getActivity(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(@StringRes int stringId) {
        Toast.makeText(getContext(), getString(stringId), Toast.LENGTH_SHORT).show();
    }
}
