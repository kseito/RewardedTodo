package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.common.ui.theme.RewardedTodoScheme
import jp.kztproject.rewardedtodo.feature.auth.AuthRoute
import jp.kztproject.rewardedtodo.feature.auth.authScreen
import jp.kztproject.rewardedtodo.feature.setting.SettingRoute
import jp.kztproject.rewardedtodo.feature.setting.settingScreen
import jp.kztproject.rewardedtodo.presentation.auth.createTodoistAuthTabLauncher

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    // ActivityResultLauncherの登録はSTARTED以降だと例外になるため、フィールド初期化時に生成する
    private val todoistAuthTabLauncher = createTodoistAuthTabLauncher(this)

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            // StateFlowの値を直接読むと再コンポーズが起きず、判定が終わっても画面が空のままになる
            val startDestination by homeViewModel.startDestination.collectAsState()
            val destination = startDestination ?: return@setContent

            MaterialTheme(
                colorScheme = RewardedTodoScheme(isDarkTheme = isSystemInDarkTheme()),
            ) {
                val startRoute: NavKey = when (destination) {
                    StartDestination.AUTH -> AuthRoute
                    StartDestination.HOME -> HomeRoute
                }
                val backStack = rememberNavBackStack(startRoute)

                // 認証画面とホーム画面は行き来させない。戻るボタンで逆戻りしないよう積み直す
                fun replaceStackWith(route: NavKey) {
                    backStack.clear()
                    backStack.add(route)
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                        rememberViewModelStoreNavEntryDecorator<NavKey>(),
                    ),
                    entryProvider = entryProvider {
                        authScreen(
                            authTabLauncher = todoistAuthTabLauncher,
                            onAuthenticated = { replaceStackWith(HomeRoute) },
                        )
                        homeScreen(
                            onClickSetting = { backStack.add(SettingRoute) },
                        )
                        settingScreen(
                            onLoggedOut = { replaceStackWith(AuthRoute) },
                        )
                    },
                )
            }
        }
    }
}
