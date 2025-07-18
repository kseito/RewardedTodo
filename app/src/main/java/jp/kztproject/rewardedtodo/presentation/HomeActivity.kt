package jp.kztproject.rewardedtodo.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.RewardedTodoBottomBar
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination
import jp.kztproject.rewardedtodo.feature.auth.TodoistAuthActivity
import jp.kztproject.rewardedtodo.feature.reward.REWARD_SCREEN
import jp.kztproject.rewardedtodo.feature.reward.rewardListScreen
import jp.kztproject.rewardedtodo.feature.setting.SettingDialog
import jp.kztproject.rewardedtodo.presentation.todo.TODO_SCREEN
import jp.kztproject.rewardedtodo.presentation.todo.todoListScreen

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            val navController = rememberNavController()
            val topLevelDestinations = TopLevelDestination.entries
            var currentDestination by remember { mutableStateOf(TopLevelDestination.TODO) }
            val onNavigateToDestination: (TopLevelDestination) -> Unit = {
                when (it) {
                    TopLevelDestination.TODO -> navController.navigateHome(TODO_SCREEN)
                    TopLevelDestination.REWARD -> navController.navigateHome(REWARD_SCREEN)
                }
                currentDestination = it
            }
            var showSettingDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Scaffold(
                topBar = {
                    TopBar(
                        currentDestination.iconTextId,
                        onSettingClicked = {
                            showSettingDialog = true
                        },
                    )
                },
                bottomBar = {
                    RewardedTodoBottomBar(topLevelDestinations, onNavigateToDestination)
                },
            ) { padding ->
                RewardedTodoApp(padding, navController)
            }

            if (showSettingDialog) {
                var todoistAuthFinished by remember { mutableStateOf(false) }
                val launcher =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.StartActivityForResult(),
                    ) {
                        todoistAuthFinished = true
                    }
                SettingDialog(
                    todoistAuthFinished = todoistAuthFinished,
                    onDismiss = {
                        showSettingDialog = false
                    },
                    onTodoistAuthStartClicked = {
                        val intent = Intent(context, TodoistAuthActivity::class.java)
                        context.startActivity(intent)
                        launcher.launch(intent)
                    },
                )
            }
        }
    }
}

@Composable
private fun RewardedTodoApp(padding: PaddingValues, navController: NavHostController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        // TODO apply Theme
        NavHost(
            navController = navController,
            startDestination = TODO_SCREEN,
        ) {
            todoListScreen()
            rewardListScreen()
        }
    }
}

private fun NavHostController.navigateHome(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateHome.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
