package jp.kztproject.rewardedtodo.feature.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTING_SCREEN = "setting_screen"

fun NavGraphBuilder.settingScreen(todoistAuthFinished: Boolean) {
    composable(SETTING_SCREEN) {
        SettingScreen(
            todoistAuthFinished = todoistAuthFinished,
        )
    }
}
