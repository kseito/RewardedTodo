package jp.kztproject.rewardedtodo.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

const val HOME_SCREEN = "home_screen"

@Serializable
data object HomeRoute : NavKey

fun NavGraphBuilder.homeScreen(onClickSetting: () -> Unit) {
    composable(HOME_SCREEN) {
        HomeScreen(onClickSetting)
    }
}

fun EntryProviderScope<NavKey>.homeScreen(onClickSetting: () -> Unit) {
    entry<HomeRoute> {
        HomeScreen(onClickSetting)
    }
}
