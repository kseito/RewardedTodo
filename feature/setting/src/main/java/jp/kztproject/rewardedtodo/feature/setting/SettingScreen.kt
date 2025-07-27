package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingScreen(
    todoistAuthFinished: Boolean = false,
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

    SettingScreenContent(
        todoistExtensionEnabled.value,
        onTodoistAuthStartClicked,
        onTodoistAuthClearClicked,
    )
}

@Composable
private fun SettingScreenContent(
    todoistExtensionEnabled: Boolean,
    onTodoistAuthStartClicked: () -> Unit,
    onTodoistAuthClearClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        
        SettingSectionTitle(text = stringResource(R.string.extensions_section))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingTodoistExtensionRow(
            todoistExtensionEnabled = todoistExtensionEnabled,
            onTodoistClicked = onTodoistAuthStartClicked,
            onTodoistClearClicked = onTodoistAuthClearClicked,
        )
    }
}

@Composable
private fun SettingSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingTodoistExtensionRow(
    onTodoistClicked: () -> Unit,
    onTodoistClearClicked: () -> Unit,
    todoistExtensionEnabled: Boolean
) {
    Row(
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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.todoist_extension_title),
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = todoistExtensionEnabled,
            onCheckedChange = null
        )
    }
}

@Composable
@Preview
fun SettingScreenPreview() {
    SettingScreenContent(
        todoistExtensionEnabled = false,
        onTodoistAuthStartClicked = {},
        onTodoistAuthClearClicked = {}
    )
}
