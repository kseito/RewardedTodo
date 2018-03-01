package kztproject.jp.splacounter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import kztproject.jp.splacounter.R;
import kztproject.jp.splacounter.preference.PrefsWrapper;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;

public class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
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

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
