package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
import jp.kztproject.rewardedtodo.presentation.auth.AuthTabTodoistAuthTabLauncher
import jp.kztproject.rewardedtodo.feature.setting.SettingRoute
import jp.kztproject.rewardedtodo.feature.setting.settingScreen

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    // ActivityResultLauncherの登録はSTARTED以降だと例外になるため、フィールド初期化時に生成する
    private val todoistAuthTabLauncher = AuthTabTodoistAuthTabLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            MaterialTheme(
                colorScheme = RewardedTodoScheme(isDarkTheme = isSystemInDarkTheme()),
            ) {
                val backStack = rememberNavBackStack(HomeRoute)
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                        rememberViewModelStoreNavEntryDecorator<NavKey>(),
                    ),
                    entryProvider = entryProvider {
                        homeScreen(
                            onClickSetting = {
                                backStack.add(SettingRoute)
                            },
                        )
                        settingScreen(authTabLauncher = todoistAuthTabLauncher)
                    },
                )
            }
        }
    }
}
