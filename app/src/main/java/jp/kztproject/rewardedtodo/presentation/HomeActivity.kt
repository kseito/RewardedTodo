package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.R
import jp.kztproject.rewardedtodo.databinding.ActivityHomeBinding
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), HomeViewModel.Callback {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel.initialize(this)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.todo_list_fragment, R.id.reward_list_fragment), binding.drawerLayout)

        //FIXME Navigation drawer menu not working
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onLogout() {
        binding.drawerLayout.closeDrawers()
        fragmentTransitionManager.transitionToAuthFragment(this)
    }
}
