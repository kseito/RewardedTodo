package kztproject.jp.splacounter;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.model.Counter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String COUNT = "count";

    @Inject
    SharedPreferences mSharedPreferences;

    @Bind(R.id.text_counter)
    TextView mTextCounter;

    @Bind(R.id.count_down_button)
    Button mCountDownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((MyApplication) getApplication()).component().inject(this);

        initCounter();
    }

    public void initCounter() {

        MyServiceClient serviceClient = new MyServiceClient();
        Observable<Counter> observable = serviceClient.getCounter();
        showCount(observable);
    }

    @OnClick(R.id.count_down_button)
    public void clickCountDown(View view) {

        MyServiceClient serviceClient = new MyServiceClient();
        Observable<Counter> observable = serviceClient.consumeCounter();
        showCount(observable);
    }

    @OnClick(R.id.reload_button)
    public void clickReload(View view) {
        initCounter();
    }

    private void showCount(Observable<Counter> observable) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Now Loading...");
        progressDialog.show();

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Counter>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Error:" + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(Counter counter) {
                        int count = GameCountUtils.convertGameCountFromCounter(counter);
                        mTextCounter.setText(String.valueOf(count));
                        if (count <= 0) {
                            mCountDownButton.setEnabled(false);
                        } else {
                            mCountDownButton.setEnabled(true);
                        }
                        System.out.println("カウント：" + counter.getCount());
                    }
                });
    }
}
