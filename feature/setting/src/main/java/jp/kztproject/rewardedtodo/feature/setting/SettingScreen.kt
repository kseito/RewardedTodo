package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingScreen(authTabLauncher: TodoistAuthTabLauncher, viewModel: SettingViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsState()

    // ViewModelが発行した認可URLをAuth Tabへ渡す
    LaunchedEffect(authTabLauncher) {
        viewModel.authorizeRequests.collect { authorizeUrl ->
            authTabLauncher.launch(authorizeUrl)
        }
    }

    // Auth Tabが返したリダイレクト結果をViewModelへ戻す
    LaunchedEffect(authTabLauncher) {
        authTabLauncher.results.collect { result ->
            viewModel.onAuthTabResult(result)
        }
    }

    SettingScreenContent(
        uiState = uiState.value,
        onConnect = {
            // 非対応ブラウザではAuth Tabが起動できないため、URLを発行する前に弾く
            if (authTabLauncher.isSupported()) {
                viewModel.connect()
            } else {
                viewModel.onAuthTabUnsupported()
            }
        },
        onDisconnect = { viewModel.disconnect() },
    )
}

@Composable
private fun SettingScreenContent(uiState: TodoistConnectionUiState, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        SettingSectionTitle(text = stringResource(R.string.extensions_section))

        Spacer(modifier = Modifier.height(16.dp))

        TodoistConnectionSection(
            uiState = uiState,
            onConnect = onConnect,
            onDisconnect = onDisconnect,
        )
    }
}

@Composable
private fun SettingSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun TodoistConnectionSection(
    uiState: TodoistConnectionUiState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ConnectionStatusCard(isConnected = uiState.isConnected)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (uiState.isConnected) {
                stringResource(R.string.todoist_disconnect_description)
            } else {
                stringResource(R.string.todoist_connect_description)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getErrorMessage(uiState.error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            if (uiState.isConnected) {
                OutlinedButton(
                    onClick = onDisconnect,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    ButtonContent(
                        isLoading = uiState.isLoading,
                        label = stringResource(R.string.disconnect_integration),
                    )
                }
            } else {
                Button(
                    onClick = onConnect,
                    enabled = !uiState.isLoading,
                ) {
                    ButtonContent(
                        isLoading = uiState.isLoading,
                        label = stringResource(R.string.connect_todoist),
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonContent(isLoading: Boolean, label: String) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
        )
    } else {
        Text(label)
    }
}

@Composable
private fun getErrorMessage(error: TodoistAuthError): String = when (error) {
    TodoistAuthError.CANCELED -> stringResource(R.string.error_auth_canceled)
    TodoistAuthError.VERIFICATION_FAILED -> stringResource(R.string.error_auth_verification_failed)
    TodoistAuthError.STATE_MISMATCH -> stringResource(R.string.error_auth_state_mismatch)
    TodoistAuthError.AUTHORIZATION_FAILED -> stringResource(R.string.error_auth_authorization_failed)
    TodoistAuthError.EXCHANGE_FAILED -> stringResource(R.string.error_auth_exchange_failed)
    TodoistAuthError.AUTH_TAB_UNSUPPORTED -> stringResource(R.string.error_auth_tab_unsupported)
    TodoistAuthError.UNKNOWN -> stringResource(R.string.error_auth_unknown)
}

@Composable
private fun ConnectionStatusCard(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isConnected) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Filled.Warning
                },
                contentDescription = null,
                tint = if (isConnected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isConnected) {
                    stringResource(R.string.status_connected)
                } else {
                    stringResource(R.string.status_disconnected)
                },
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
@Preview
fun SettingScreenPreview() {
    SettingScreenContent(
        uiState = TodoistConnectionUiState(),
        onConnect = {},
        onDisconnect = {},
    )
}

@Composable
@Preview
fun SettingScreenConnectedPreview() {
    SettingScreenContent(
        uiState = TodoistConnectionUiState(isConnected = true),
        onConnect = {},
        onDisconnect = {},
    )
}

@Composable
@Preview
fun SettingScreenAuthorizingPreview() {
    SettingScreenContent(
        uiState = TodoistConnectionUiState(isLoading = true),
        onConnect = {},
        onDisconnect = {},
    )
}

@Composable
@Preview
fun SettingScreenAuthErrorPreview() {
    SettingScreenContent(
        uiState = TodoistConnectionUiState(error = TodoistAuthError.CANCELED),
        onConnect = {},
        onDisconnect = {},
    )
}
