package kztproject.jp.splacounter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.preference.PrefsWrapper;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ((MyApplication) getApplication()).component().inject(this);
        if (PrefsWrapper.INSTANCE.getUserId() != 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PlayFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AuthFragment.newInstance())
                    .commit();
        }
    }
}