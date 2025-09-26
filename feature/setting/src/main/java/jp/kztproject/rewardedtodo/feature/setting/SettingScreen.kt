package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingScreen(todoistAuthFinished: Boolean = false, viewModel: SettingViewModel = hiltViewModel()) {
    val todoistExtensionEnabled = viewModel.hasAccessToken.collectAsState()
    val tokenUiState = viewModel.tokenUiState.collectAsState()

    if (todoistAuthFinished) {
        viewModel.loadAccessToken()
    }

    SettingScreenContent(
        todoistExtensionEnabled.value,
        tokenUiState.value,
        onTokenInputChange = { viewModel.updateTokenInput(it) },
        onTokenValidate = { viewModel.saveToken() },
        onTokenDelete = { viewModel.deleteToken() },
    )
}

@Composable
private fun SettingScreenContent(
    todoistExtensionEnabled: Boolean,
    tokenUiState: TokenSettingsUiState,
    onTokenInputChange: (String) -> Unit,
    onTokenValidate: () -> Unit,
    onTokenDelete: () -> Unit,
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

        SettingSectionTitle(text = stringResource(R.string.extensions_section))

        Spacer(modifier = Modifier.height(16.dp))

        TodoistTokenSection(
            isConnected = todoistExtensionEnabled,
            tokenUiState = tokenUiState,
            onTokenInputChange = onTokenInputChange,
            onTokenValidate = onTokenValidate,
            onTokenDelete = onTokenDelete,
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
private fun TodoistTokenSection(
    isConnected: Boolean,
    tokenUiState: TokenSettingsUiState,
    onTokenInputChange: (String) -> Unit,
    onTokenValidate: () -> Unit,
    onTokenDelete: () -> Unit,
) {
    var isTokenVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Connection Status Card
        ConnectionStatusCard(isConnected = isConnected)

        Spacer(modifier = Modifier.height(16.dp))

        // Token Input Field
        OutlinedTextField(
            value = tokenUiState.tokenInput,
            onValueChange = onTokenInputChange,
            label = { Text(stringResource(R.string.api_token_label)) },
            placeholder = { Text(stringResource(R.string.api_token_placeholder)) },
            visualTransformation = if (isTokenVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                Row {
                    // Visibility toggle
                    IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                        Icon(
                            imageVector = if (isTokenVisible) {
                                Icons.Filled.Lock
                            } else {
                                Icons.Filled.Edit
                            },
                            contentDescription = if (isTokenVisible) stringResource(R.string.hide_token) else stringResource(R.string.show_token),
                        )
                    }
                    // Clear button
                    if (tokenUiState.tokenInput.isNotEmpty()) {
                        IconButton(onClick = { onTokenInputChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_token))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isConnected,
            isError = tokenUiState.validationError != null,
        )

        // Error message
        if (tokenUiState.validationError != null) {
            Text(
                text = getErrorMessage(tokenUiState.validationError),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            if (isConnected) {
                OutlinedButton(
                    onClick = onTokenDelete,
                    enabled = !tokenUiState.isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    if (tokenUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(stringResource(R.string.disconnect_integration))
                    }
                }
            } else {
                Button(
                    onClick = onTokenValidate,
                    enabled = tokenUiState.tokenInput.isNotEmpty() && !tokenUiState.isLoading,
                ) {
                    if (tokenUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(stringResource(R.string.verify_connection))
                    }
                }
            }
        }
    }
}

@Composable
private fun getErrorMessage(error: TokenValidationError): String {
    return when (error) {
        TokenValidationError.TOKEN_EMPTY -> stringResource(R.string.error_token_empty)
        TokenValidationError.INVALID_TOKEN_FORMAT -> stringResource(R.string.error_invalid_token_format)
        TokenValidationError.FAILED_TO_SAVE_TOKEN -> stringResource(R.string.error_failed_save_token)
    }
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
                text = if (isConnected) stringResource(R.string.status_connected) else stringResource(R.string.status_disconnected),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
@Preview(name = "Disconnected State")
fun SettingScreenPreview() {
    SettingScreenContent(
        todoistExtensionEnabled = false,
        tokenUiState = TokenSettingsUiState(),
        onTokenInputChange = {},
        onTokenValidate = {},
        onTokenDelete = {},
    )
}

@Composable
@Preview(name = "Connected State")
fun SettingScreenConnectedPreview() {
    SettingScreenContent(
        todoistExtensionEnabled = true,
        tokenUiState = TokenSettingsUiState(hasToken = true, isConnected = true),
        onTokenInputChange = {},
        onTokenValidate = {},
        onTokenDelete = {},
    )
}
