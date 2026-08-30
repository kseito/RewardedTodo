package jp.kztproject.rewardedtodo.feature.setting

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SettingRoute : NavKey

fun EntryProviderScope<NavKey>.settingScreen(authTabLauncher: TodoistAuthTabLauncher) {
    entry<SettingRoute> {
        SettingScreen(authTabLauncher = authTabLauncher)
    }
}
