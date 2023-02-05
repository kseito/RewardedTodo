package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardListScreenWithBottomSheet
import jp.kztproject.rewardedtodo.presentation.todo.TodoListScreenWithBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    companion object {
        private const val TODO_SCREEN = "todo_screen"
        private const val REWARD_SCREEN = "reward_screen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onSettingCLicked = {
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
                        onSettingClicked = onSettingCLicked,
                    )
                },
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
                            TodoListScreenWithBottomSheet()
                        }

                        composable(route = REWARD_SCREEN) {
                            RewardListScreenWithBottomSheet()
                        }
                    }
                }
            }
        }
    }
}
