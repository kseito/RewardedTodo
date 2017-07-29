package kztproject.jp.splacounter.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.preference.AppPrefsProvider;
import kztproject.jp.splacounter.viewmodel.PlayViewModel;

/**
 * Created by k-seito on 2017/07/29.
 */

public class PlayFragment extends Fragment implements PlayViewModel.Callback{

    public static final String COUNT = "count";

    @Inject
    MyServiceClient serviceClient;

    @Inject
    AppPrefsProvider prefs;

    @Bind(R.id.text_counter)
    TextView mTextCounter;

    @Bind(R.id.count_down_button)
    Button mCountDownButton;

    @Inject
    PlayViewModel viewModel;

    ProgressDialog progressDialog;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MyApplication) getActivity().getApplication()).component().inject(this);
        viewModel.setCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Now Loading...");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initCounter();
    }

    public void initCounter() {
        viewModel.getGameCount();
    }

    @OnClick(R.id.count_down_button)
    public void clickCountDown(View view) {
        viewModel.consumeGameCount();
    }

    @OnClick(R.id.reload_button)
    public void clickReload(View view) {
        initCounter();
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void countDownGameCount(int gameCount) {
        mTextCounter.setText(String.valueOf(gameCount));
        if (gameCount <= 0) {
            mCountDownButton.setEnabled(false);
        } else {
            mCountDownButton.setEnabled(true);
        }
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
