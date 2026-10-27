package jp.kztproject.rewardedtodo

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RewardedTodoBottomBar(
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentDestination,
                onClick = { onNavigateToDestination(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
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
