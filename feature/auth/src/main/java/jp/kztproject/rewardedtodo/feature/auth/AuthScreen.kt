package jp.kztproject.rewardedtodo.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Todoistと連携するまで他の機能に進ませない画面。
 *
 * 連携が前提のアプリなので、スキップする導線は置かない。
 */
@Composable
fun AuthScreen(
    authTabLauncher: TodoistAuthTabLauncher,
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ViewModelが発行した認可URLをAuth Tabへ渡し、渡し終えたら状態から消す
    LaunchedEffect(uiState.authorizeUrl) {
        uiState.authorizeUrl?.let { authorizeUrl ->
            authTabLauncher.launch(authorizeUrl)
            viewModel.consumeAuthorizeUrl()
        }
    }

    // Auth Tabが返したリダイレクト結果をViewModelへ戻す
    LaunchedEffect(authTabLauncher) {
        authTabLauncher.results.collect { result ->
            viewModel.onAuthTabResult(result)
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
            viewModel.consumeAuthenticated()
        }
    }

    AuthScreenContent(
        uiState = uiState,
        onConnect = {
            // 非対応ブラウザではAuth Tabが起動できないため、URLを発行する前に弾く
            if (authTabLauncher.isSupported()) {
                viewModel.connect()
            } else {
                viewModel.onAuthTabUnsupported()
            }
        },
    )
}

@Composable
private fun AuthScreenContent(uiState: AuthUiState, onConnect: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.auth_app_name),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.auth_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onConnect,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.auth_connect_button))
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage(uiState.error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun errorMessage(error: AuthError): String = when (error) {
    AuthError.CANCELED -> stringResource(R.string.error_auth_canceled)
    AuthError.VERIFICATION_FAILED -> stringResource(R.string.error_auth_verification_failed)
    AuthError.STATE_MISMATCH -> stringResource(R.string.error_auth_state_mismatch)
    AuthError.AUTHORIZATION_FAILED -> stringResource(R.string.error_auth_authorization_failed)
    AuthError.EXCHANGE_FAILED -> stringResource(R.string.error_auth_exchange_failed)
    AuthError.AUTH_TAB_UNSUPPORTED -> stringResource(R.string.error_auth_tab_unsupported)
    AuthError.UNKNOWN -> stringResource(R.string.error_auth_unknown)
}

@Composable
@Preview
fun AuthScreenPreview() {
    AuthScreenContent(uiState = AuthUiState(), onConnect = {})
}

@Composable
@Preview
fun AuthScreenLoadingPreview() {
    AuthScreenContent(uiState = AuthUiState(isLoading = true), onConnect = {})
}

@Composable
@Preview
fun AuthScreenErrorPreview() {
    AuthScreenContent(uiState = AuthUiState(error = AuthError.CANCELED), onConnect = {})
}
