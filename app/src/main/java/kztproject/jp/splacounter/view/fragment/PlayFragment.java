package kztproject.jp.splacounter.view.fragment;

import android.app.ProgressDialog;
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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.domain.GameCountUtils;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

/**
 * Created by k-seito on 2017/07/29.
 */

public class PlayFragment extends Fragment {

    public static final String COUNT = "count";

    @Inject
    MyServiceClient serviceClient;

    @Inject
    AppPrefsProvider prefs;

    @Bind(R.id.text_counter)
    TextView mTextCounter;

    @Bind(R.id.count_down_button)
    Button mCountDownButton;

    public static PlayFragment newInstance() {

        Bundle args = new Bundle();

        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).component().inject(this);

        initCounter();
    }

    public void initCounter() {

        Observable<Counter> observable = serviceClient.getCounter(prefs.get().getUserId());
        showCount(observable);
    }

    @OnClick(R.id.count_down_button)
    public void clickCountDown(View view) {
        Observable<Counter> observable = serviceClient.consumeCounter(prefs.get().getUserId());
        showCount(observable);
    }

    @OnClick(R.id.reload_button)
    public void clickReload(View view) {
        initCounter();
    }

    private void showCount(Observable<Counter> observable) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Now Loading...");
        progressDialog.show();

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                })
                .subscribe(
                        counter -> {
                            int count = GameCountUtils.convertGameCountFromCounter(counter);
                            mTextCounter.setText(String.valueOf(count));
                            if (count <= 0) {
                                mCountDownButton.setEnabled(false);
                            } else {
                                mCountDownButton.setEnabled(true);
                            }
                            System.out.println("カウント：" + counter.getCount());
                        },
                        e -> {
                            System.out.println("Error:" + e.getMessage());
                            Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        },
                        () -> {
                            System.out.println("Completed!");
                        }
                );
    }

}
