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
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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

    HomeScreenContent(
        currentDestination = currentDestination,
        topLevelDestinations = topLevelDestinations,
        onNavigateToDestination = onNavigateToDestination,
        onClickSetting = onClickSetting,
    ) { padding ->
        RewardedTodoApp(padding, navController)
    }
}

@Composable
private fun HomeScreenContent(
    currentDestination: TopLevelDestination,
    topLevelDestinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    onClickSetting: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
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
        content(padding)
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

@Composable
@Preview
fun HomeScreenTodoTabPreview() {
    HomeScreenContent(
        currentDestination = TopLevelDestination.TODO,
        topLevelDestinations = TopLevelDestination.entries,
        onNavigateToDestination = {},
        onClickSetting = {},
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

@Composable
@Preview
fun HomeScreenRewardTabPreview() {
    HomeScreenContent(
        currentDestination = TopLevelDestination.REWARD,
        topLevelDestinations = TopLevelDestination.entries,
        onNavigateToDestination = {},
        onClickSetting = {},
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
