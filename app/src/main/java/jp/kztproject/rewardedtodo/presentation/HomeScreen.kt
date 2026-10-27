package jp.kztproject.rewardedtodo.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import jp.kztproject.rewardedtodo.RewardedTodoBottomBar
import jp.kztproject.rewardedtodo.TopBar
import jp.kztproject.rewardedtodo.TopLevelDestination
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme

import jp.kztproject.rewardedtodo.feature.reward.RewardListRoute
import jp.kztproject.rewardedtodo.feature.reward.rewardListScreen
import jp.kztproject.rewardedtodo.presentation.todo.TodoListRoute
import jp.kztproject.rewardedtodo.presentation.todo.todoListScreen

@Composable
fun HomeScreen(onClickSetting: () -> Unit) {
    val navigationState = rememberNavigationState(
        startRoute = TodoListRoute,
        topLevelRoutes = setOf(TodoListRoute, RewardListRoute),
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val topLevelDestinations = TopLevelDestination.entries
    val currentDestination = when (navigationState.topLevelRoute) {
        RewardListRoute -> TopLevelDestination.REWARD
        else -> TopLevelDestination.TODO
    }
    val onNavigateToDestination: (TopLevelDestination) -> Unit = {
        when (it) {
            TopLevelDestination.TODO -> navigator.navigate(TodoListRoute)
            TopLevelDestination.REWARD -> navigator.navigate(RewardListRoute)
        }
    }

    HomeScreenContent(
        currentDestination = currentDestination,
        topLevelDestinations = topLevelDestinations,
        onNavigateToDestination = onNavigateToDestination,
        onClickSetting = onClickSetting,
    ) { padding ->
        RewardedTodoApp(padding, navigationState, navigator, onClickSetting)
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                currentDestination.iconTextId,
                onSettingClicked = onClickSetting,
            )
        },
        bottomBar = {
            RewardedTodoBottomBar(topLevelDestinations, currentDestination, onNavigateToDestination)
        },
    ) { padding ->
        content(padding)
    }
}

@Composable
private fun RewardedTodoApp(
    padding: PaddingValues,
    navigationState: NavigationState,
    navigator: Navigator,
    onOpenSetting: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        // TODO apply Theme
        NavDisplay(
            entries = navigationState.toEntries(
                entryProvider {
                    todoListScreen(onOpenSetting = onOpenSetting)
                    rewardListScreen()
                },
            ),
            onBack = { navigator.goBack() },
        )
    }
}

@Composable
@Preview
fun HomeScreenTodoTabPreview() {
    HomeScreenThemedPreview(TopLevelDestination.TODO)
}

@Composable
@Preview
fun HomeScreenRewardTabPreview() {
    HomeScreenThemedPreview(TopLevelDestination.REWARD)
}

@Composable
private fun HomeScreenThemedPreview(currentDestination: TopLevelDestination) {
    MaterialTheme(colorScheme = RewardedTodoScheme(isDarkTheme = false)) {
        HomeScreenContent(
            currentDestination = currentDestination,
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
}
