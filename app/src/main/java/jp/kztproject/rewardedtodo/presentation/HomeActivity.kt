package jp.kztproject.rewardedtodo.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.RewardedTodoBottomBar
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination
import jp.kztproject.rewardedtodo.feature.setting.SettingDialog
import jp.kztproject.rewardedtodo.presentation.auth.todoist.TodoistAuthActivity
import jp.kztproject.rewardedtodo.presentation.reward.REWARD_SCREEN
import jp.kztproject.rewardedtodo.presentation.reward.rewardListScreen
import jp.kztproject.rewardedtodo.presentation.todo.TODO_SCREEN
import jp.kztproject.rewardedtodo.presentation.todo.todoListScreen

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            var showSettingDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Scaffold(
                topBar = {
                    TopBar(
                        currentDestination.iconTextId,
                        onSettingClicked = {
                            showSettingDialog = true
                        }
                    )
                },
                bottomBar = {
                    RewardedTodoBottomBar(topLevelDestinations, onNavigateToDestination)
                }
            ) { padding ->
                RewardedTodoApp(padding, navController)
            }

            if (showSettingDialog) {
                var todoistAuthFinished by remember { mutableStateOf(false) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
                    }
                )
            }
        }
    }
}

@Composable
private fun RewardedTodoApp(
    padding: PaddingValues,
    navController: NavHostController
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
