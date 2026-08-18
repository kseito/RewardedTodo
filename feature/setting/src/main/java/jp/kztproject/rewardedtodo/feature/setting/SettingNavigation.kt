package jp.kztproject.rewardedtodo.feature.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

const val SETTING_SCREEN = "setting_screen"

@Serializable
data object SettingRoute : NavKey

fun NavGraphBuilder.settingScreen() {
    composable(SETTING_SCREEN) {
        SettingScreen()
    }
}

fun EntryProviderScope<NavKey>.settingScreen() {
    entry<SettingRoute> {
        SettingScreen()
    }
}
