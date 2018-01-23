package kztproject.jp.splacounter.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.OnClick;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.activity.RewardActivity;
import kztproject.jp.splacounter.api.MiniatureGardenClient;
import kztproject.jp.splacounter.databinding.FragmentPlayBinding;
import kztproject.jp.splacounter.viewmodel.PlayViewModel;

public class PlayFragment extends Fragment implements PlayViewModel.Callback{

    @Inject
    MiniatureGardenClient serviceClient;

    @Inject
    PlayViewModel viewModel;

    ProgressDialog progressDialog;

    FragmentPlayBinding binding;

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
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Now Loading...");

        return binding.getRoot();
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
    public void showGameCount(int gameCount) {
        binding.textCounter.setText(String.valueOf(gameCount));
        if (gameCount <= 0) {
            binding.countDownButton.setEnabled(false);
        } else {
            binding.countDownButton.setEnabled(true);
        }
    }

    @Override
    public void showReward() {
        startActivity(RewardActivity.Companion.createIntent(getContext()));
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}