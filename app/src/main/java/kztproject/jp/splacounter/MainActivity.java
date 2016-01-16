package kztproject.jp.splacounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text_counter)
    TextView mTextCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.count_up_button)
    public void clickCountUp(View view) {
        int count = Integer.parseInt(mTextCounter.getText().toString());
        mTextCounter.setText(String.valueOf(count + 1));
    }

    @OnClick(R.id.count_down_button)
    public void clickCountDown(View view) {
        int count = Integer.parseInt(mTextCounter.getText().toString());
        if (count == 0) {
            return;
        }
        mTextCounter.setText(String.valueOf(count - 1));
    }

}
