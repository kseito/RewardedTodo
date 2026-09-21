package jp.kztproject.rewardedtodo.feature.auth

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute : NavKey

fun EntryProviderScope<NavKey>.authScreen(authTabLauncher: TodoistAuthTabLauncher, onAuthenticated: () -> Unit) {
    entry<AuthRoute> {
        AuthScreen(authTabLauncher = authTabLauncher, onAuthenticated = onAuthenticated)
    }
}
