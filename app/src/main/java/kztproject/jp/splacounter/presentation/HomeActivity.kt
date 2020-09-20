package kztproject.jp.splacounter.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kztproject.jp.splacounter.R
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), HomeViewModel.Callback, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        viewModel.initialize(this)

        val bottomNavigationView = findViewById<NavigationView>(R.id.navigation_view)
        bottomNavigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> viewModel.logout()
            }
            false

        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }

    override fun onLogout() {
        fragmentTransitionManager.transitionToAuthFragment(this)
    }
}
