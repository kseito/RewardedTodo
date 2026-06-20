package jp.kztproject.rewardedtodo

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RewardedTodoBottomBar(
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentDestination,
                onClick = { onNavigateToDestination(destination) },
                label = { Text(stringResource(destination.iconTextId)) },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconImageId),
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun RewardedTodoBottomBarPreview() {
    RewardedTodoBottomBar(
        topLevelDestinations = TopLevelDestination.entries,
        currentDestination = TopLevelDestination.TODO,
        onNavigateToDestination = {},
    )
}
