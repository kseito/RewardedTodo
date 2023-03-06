package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Switch
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingDialog(
    onDismiss: () -> Unit,
    onTodoistClicked: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val todoistExtensionEnabled = viewModel.hasAccessToken.collectAsState()

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Divider(Modifier.padding(vertical = 8.dp))
                SettingDialogSectionTitle(text = stringResource(R.string.extensions_section))
                SettingDialogTodoistExtensionRow(
                    todoistExtensionEnabled = todoistExtensionEnabled.value,
                    onTodoistClicked = onTodoistClicked
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                text = stringResource(android.R.string.ok),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onDismiss() },
            )
        },
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp)
    )
}

@Composable
private fun SettingDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
    )
}

@Composable
private fun SettingDialogTodoistExtensionRow(
    onTodoistClicked: () -> Unit,
    todoistExtensionEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onTodoistClicked
            )
    ) {
        Text(text = stringResource(R.string.todoist_extension_title))

        Switch(
            checked = todoistExtensionEnabled,
            onCheckedChange = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)

        )
    }
}

@Composable
@Preview
fun SettingDialogPreview() {
    SettingDialog(
        onDismiss = {},
        onTodoistClicked = {}
    )
}
