package kztproject.jp.splacounter.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.preference.PrefsWrapper
import kztproject.jp.splacounter.view.fragment.AuthFragment
import kztproject.jp.splacounter.view.fragment.PlayFragment
import javax.inject.Inject

class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        if (PrefsWrapper.userId != 0) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PlayFragment.newInstance())
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AuthFragment.newInstance())
                    .commit()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }
}
