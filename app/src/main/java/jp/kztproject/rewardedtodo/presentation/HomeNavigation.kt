package jp.kztproject.rewardedtodo.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME_SCREEN = "home_screen"

fun NavGraphBuilder.homeScreen(onClickSetting: () -> Unit) {
    composable(HOME_SCREEN) {
        HomeScreen(onClickSetting)
    }
}
