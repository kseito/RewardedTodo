package kztproject.jp.splacounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String COUNT = "count";

    @Inject
    SharedPreferences mSharedPreferences;

    @Bind(R.id.text_counter)
    TextView mTextCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((MyApplication) getApplication()).component().inject(this);

        initCounter();
    }

    public void initCounter() {
        int count = mSharedPreferences.getInt(COUNT, 0);
        mTextCounter.setText(String.valueOf(count));
    }

    @OnClick(R.id.count_up_button)
    public void clickCountUp(View view) {
        int count = Integer.parseInt(mTextCounter.getText().toString()) + 1;
        mTextCounter.setText(String.valueOf(count));
        saveCount(count);
    }

    @OnClick(R.id.count_down_button)
    public void clickCountDown(View view) {
        int count = Integer.parseInt(mTextCounter.getText().toString()) - 1;
        if (count < 0) {
            return;
        }
        mTextCounter.setText(String.valueOf(count));
        saveCount(count);
    }

    private void saveCount(int count) {
        mSharedPreferences.edit().putInt(COUNT, count).commit();
    }
}
