package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.* // ktlint-disable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.* // ktlint-disable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.RewardedTodoBottomBar
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination
import jp.kztproject.rewardedtodo.presentation.reward.REWARD_SCREEN
import jp.kztproject.rewardedtodo.presentation.reward.rewardListScreen
import jp.kztproject.rewardedtodo.presentation.todo.TODO_SCREEN
import jp.kztproject.rewardedtodo.presentation.todo.todoListScreen

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onSettingClicked = {
            // TODO show SettingScreen
            Toast.makeText(this, "Swho SettingScreen", Toast.LENGTH_LONG).show()
        }

        setContent {
            val navController = rememberNavController()
            val topLevelDestinations = TopLevelDestination.values().asList()
            var currentDestination by remember { mutableStateOf(TopLevelDestination.TODO) }
            val onNavigateToDestination: (TopLevelDestination) -> Unit = {
                when (it) {
                    TopLevelDestination.TODO -> navController.navigate(TODO_SCREEN)
                    TopLevelDestination.REWARD -> navController.navigate(REWARD_SCREEN)
                }
                currentDestination = it
            }

            Scaffold(
                topBar = {
                    TopBar(
                        currentDestination.iconTextId,
                        onSettingClicked = onSettingClicked
                    )
                },
                bottomBar = {
                    RewardedTodoBottomBar(topLevelDestinations, onNavigateToDestination)
                }
            ) { padding ->
                RewardedTodoApp(padding, navController)
            }
        }
    }
}

@Composable
private fun RewardedTodoApp(
    padding: PaddingValues,
    navController: NavHostController,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // TODO apply Theme
        NavHost(
            navController = navController,
            startDestination = TODO_SCREEN
        ) {
            todoListScreen()
            rewardListScreen()
        }
    }
}
