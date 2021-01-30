package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import jp.kztproject.rewardedtodo.R
import jp.kztproject.rewardedtodo.databinding.ActivityHomeBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), HomeViewModel.Callback, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        viewModel.initialize(this)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> viewModel.logout()
                R.id.todoist_auth_fragment -> NavigationUI.onNavDestinationSelected(item, findNavController(R.id.nav_host_fragment))
            }
            false
        }
        val navController = Navigation.findNavController(this@HomeActivity, R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }

    override fun onLogout() {
        binding.drawerLayout.closeDrawers()
        fragmentTransitionManager.transitionToAuthFragment(this)
    }
}
