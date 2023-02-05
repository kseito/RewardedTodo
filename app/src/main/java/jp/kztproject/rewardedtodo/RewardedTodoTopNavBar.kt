package jp.kztproject.rewardedtodo

import androidx.annotation.StringRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBar(
    @StringRes titleResourceId: Int,
    onSettingClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = titleResourceId))
        },
        actions = {
            IconButton(
                onClick = onSettingClicked
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
fun TopBarPreview() {
    val destination = TopLevelDestination.TODO
    TopBar(
        titleResourceId = destination.iconTextId,
        onSettingClicked = {},
    )
}
