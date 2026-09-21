package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingScreen(onLoggedOut: () -> Unit, viewModel: SettingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loggedOut.collect { onLoggedOut() }
    }

    SettingScreenContent(
        uiState = uiState,
        onRequestLogout = { viewModel.requestLogout() },
        onConfirmLogout = { viewModel.logout() },
        onDismissLogout = { viewModel.dismissLogoutConfirmation() },
    )
}

@Composable
private fun SettingScreenContent(
    uiState: SettingUiState,
    onRequestLogout: () -> Unit,
    onConfirmLogout: () -> Unit,
    onDismissLogout: () -> Unit,
) {
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

        Text(
            text = stringResource(R.string.account_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = stringResource(R.string.logout_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.error_logout_failed),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            OutlinedButton(
                onClick = onRequestLogout,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }

    if (uiState.isConfirmingLogout) {
        LogoutConfirmationDialog(onConfirm = onConfirmLogout, onDismiss = onDismissLogout)
    }
}

@Composable
private fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.logout_confirmation_title)) },
        text = { Text(stringResource(R.string.logout_confirmation_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.logout))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
@Preview
fun SettingScreenPreview() {
    SettingScreenContent(
        uiState = SettingUiState(),
        onRequestLogout = {},
        onConfirmLogout = {},
        onDismissLogout = {},
    )
}

@Composable
@Preview
fun SettingScreenLoggingOutPreview() {
    SettingScreenContent(
        uiState = SettingUiState(isLoading = true),
        onRequestLogout = {},
        onConfirmLogout = {},
        onDismissLogout = {},
    )
}

@Composable
@Preview
fun SettingScreenLogoutConfirmationPreview() {
    SettingScreenContent(
        uiState = SettingUiState(isConfirmingLogout = true),
        onRequestLogout = {},
        onConfirmLogout = {},
        onDismissLogout = {},
    )
}
