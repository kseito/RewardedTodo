package kztproject.jp.splacounter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import kztproject.jp.splacounter.MyApplication;
import kztproject.jp.splacounter.preference.AppPrefsProvider;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;

/**
 * Created by k-seito on 2017/03/18.
 */

public class AuthActivity extends AppCompatActivity {

    @Inject
    AppPrefsProvider prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).component().inject(this);
        if (prefs.get().getUserId() != 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, PlayFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, AuthFragment.newInstance())
                    .commit();
        }
    }
}
