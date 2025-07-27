package jp.kztproject.rewardedtodo.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import jp.kztproject.rewardedtodo.RewardedTodoBottomBar
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination

import jp.kztproject.rewardedtodo.feature.reward.REWARD_SCREEN
import jp.kztproject.rewardedtodo.feature.reward.rewardListScreen
import jp.kztproject.rewardedtodo.presentation.todo.TODO_SCREEN
import jp.kztproject.rewardedtodo.presentation.todo.todoListScreen

@Composable
fun HomeScreen(onClickSetting: () -> Unit) {
    val navController = rememberNavController()
    val topLevelDestinations = TopLevelDestination.entries
    var currentDestination by rememberSaveable { mutableStateOf(TopLevelDestination.TODO) }
    val onNavigateToDestination: (TopLevelDestination) -> Unit = {
        when (it) {
            TopLevelDestination.TODO -> navController.navigateHome(TODO_SCREEN)
            TopLevelDestination.REWARD -> navController.navigateHome(REWARD_SCREEN)
        }
        currentDestination = it
    }

    Scaffold(
        topBar = {
            TopBar(
                currentDestination.iconTextId,
                onSettingClicked = onClickSetting,
            )
        },
        bottomBar = {
            RewardedTodoBottomBar(topLevelDestinations, onNavigateToDestination)
        },
    ) { padding ->
        RewardedTodoApp(padding, navController)
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
