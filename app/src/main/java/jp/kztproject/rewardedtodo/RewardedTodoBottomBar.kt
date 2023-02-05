package jp.kztproject.rewardedtodo

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun RewardedTodoBottomBar(
    topLevelDestinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar() {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                // FIXME reference appropriate value
                selected = true,
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
