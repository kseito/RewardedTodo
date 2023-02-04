package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListScreenWithBottomSheet
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.presentation.todo.TodoListScreenWithBottomSheet
import jp.kztproject.rewardedtodo.presentation.todo.TodoListViewModel
import project.seito.screen_transition.IFragmentsTransitionManager
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    companion object {
        private const val TODO_SCREEN = "todo_screen"
        private const val REWARD_SCREEN = "reward_screen"
    }

    // TODO use hiltViewModel() in each screens
    private val todoListViewModel: TodoListViewModel by viewModels()
    private val rewardListViewModel: RewardListViewModel by viewModels()

    @Inject
    lateinit var fragmentTransitionManager: IFragmentsTransitionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            // TODO apply Theme
            // TODO show bottom nav bar
            NavHost(
                navController = navController,
                startDestination = TODO_SCREEN
            ) {
                composable(route = TODO_SCREEN) {
                    TodoListScreenWithBottomSheet(
                        viewModel = todoListViewModel,
                    )
                }

                composable(route = REWARD_SCREEN) {
                    RewardListScreenWithBottomSheet(
                        viewModel = rewardListViewModel,
                    )
                }
            }
        }
    }
}
