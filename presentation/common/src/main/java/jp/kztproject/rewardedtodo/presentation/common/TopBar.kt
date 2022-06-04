package jp.kztproject.rewardedtodo.presentation.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = {
                    println("設定画面を開く")
                }
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        }
    )
}
