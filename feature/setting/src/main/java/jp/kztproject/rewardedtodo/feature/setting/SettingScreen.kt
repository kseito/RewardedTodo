package jp.kztproject.rewardedtodo.feature.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
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
fun SettingScreen(
    todoistAuthFinished: Boolean = false,
    onTodoistAuthStartClicked: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
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
            onTokenValidate = onTodoistAuthStartClicked,
            onTokenDelete = onTodoistAuthClearClicked,
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
    onTokenValidate: () -> Unit,
    onTokenDelete: () -> Unit,
) {
    var tokenInput by remember { mutableStateOf("") }
    var isTokenVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Connection Status Card
        ConnectionStatusCard(
            isConnected = isConnected,
            lastSyncTime = if (isConnected) "2時間前" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Token Input Field
        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = { Text("API Token") },
            placeholder = { Text("例: 0123456789abcdef...") },
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
                            contentDescription = if (isTokenVisible) "Hide token" else "Show token"
                        )
                    }
                    // Clear button
                    if (tokenInput.isNotEmpty()) {
                        IconButton(onClick = { tokenInput = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear token")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isConnected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isConnected) {
                OutlinedButton(
                    onClick = onTokenDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("連携を解除")
                }
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        onTokenValidate()
                        // Reset loading state - this would be handled by ViewModel in real implementation
                        isLoading = false
                    },
                    enabled = tokenInput.isNotEmpty() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("接続を確認")
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    isConnected: Boolean,
    lastSyncTime: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
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
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isConnected) "接続済み" else "未接続",
                    style = MaterialTheme.typography.titleMedium
                )
                lastSyncTime?.let {
                    Text(
                        text = "最終同期: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun SettingScreenPreview() {
    SettingScreenContent(
        todoistExtensionEnabled = false,
        onTodoistAuthStartClicked = {},
        onTodoistAuthClearClicked = {},
    )
}
