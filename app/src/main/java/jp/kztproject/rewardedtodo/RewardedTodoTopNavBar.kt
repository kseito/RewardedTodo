package jp.kztproject.rewardedtodo

import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    @StringRes titleResourceId: Int,
    onSettingClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = titleResourceId))
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colors.primary,
            titleContentColor = MaterialTheme.colors.onPrimary
        ),
        actions = {
            IconButton(
                onClick = onSettingClicked
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
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
        onSettingClicked = {}
    )
}
