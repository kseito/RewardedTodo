package jp.kztproject.rewardedtodo.presentation.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBar(
    onTodoClicked: () -> Unit,
    onRewardClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = onTodoClicked
            ) {
                Icon(painterResource(id = R.drawable.reward_done), contentDescription = null)
            }
            IconButton(
                onClick = onRewardClicked
            ) {
                Icon(painterResource(id = R.drawable.lottery_button), contentDescription = null)
            }
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

@Preview
@Composable
fun TopBarPreview() {
    TopBar({}, {})
}
