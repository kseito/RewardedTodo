package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.TopLevelDestination
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
            val topLevelDestinations = TopLevelDestination.values().asList()
            val onNavigateToDestination: (TopLevelDestination) -> Unit = {
                when(it) {
                    TopLevelDestination.TODO -> navController.navigate(TODO_SCREEN)
                    TopLevelDestination.REWARD -> navController.navigate(REWARD_SCREEN)
                }
            }

            Scaffold(
                bottomBar = {
                    // TODO write as another method
                    NavigationBar() {
                        topLevelDestinations.forEach { destination ->
                            NavigationBarItem(
                                selected = true,    // FIXME reference appropriate value
                                onClick = { onNavigateToDestination(destination) },
                                label = { Text(stringResource(destination.iconTextId)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = destination.iconImageId),
                                        contentDescription = null,
                                    )
                                }
                            )
                        }
                    }
                }
            ) { padding ->
                // TODO write as another method
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // TODO apply Theme
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
    }
}
