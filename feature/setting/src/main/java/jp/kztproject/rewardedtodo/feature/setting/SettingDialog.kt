package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Switch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingDialog(
    todoistAuthFinished: Boolean,
    onDismiss: () -> Unit,
    onTodoistAuthStartClicked: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val todoistExtensionEnabled = viewModel.hasAccessToken.collectAsState()
    val onTodoistAuthClearClicked: () -> Unit = {
        viewModel.clearAccessToken()
    }
    if (todoistAuthFinished) {
        viewModel.loadAccessToken()
    }

    SettingDialog(
        todoistExtensionEnabled.value,
        onTodoistAuthStartClicked,
        onTodoistAuthClearClicked,
        onDismiss,
    )
}

@Composable
private fun SettingDialog(
    todoistExtensionEnabled: Boolean,
    onTodoistAuthStartClicked: () -> Unit,
    onTodoistAuthClearClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                SettingDialogSectionTitle(text = stringResource(R.string.extensions_section))
                SettingDialogTodoistExtensionRow(
                    todoistExtensionEnabled = todoistExtensionEnabled,
                    onTodoistClicked = onTodoistAuthStartClicked,
                    onTodoistClearClicked = onTodoistAuthClearClicked,
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
    onTodoistClearClicked: () -> Unit,
    todoistExtensionEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = {
                    if (todoistExtensionEnabled) {
                        onTodoistClearClicked()
                    } else {
                        onTodoistClicked()
                    }
                }
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
        todoistExtensionEnabled = false,
        onTodoistAuthStartClicked = {},
        onTodoistAuthClearClicked = {},
        onDismiss = {}
    )
}
