package jp.kztproject.rewardedtodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.feature.setting.SETTING_SCREEN
import jp.kztproject.rewardedtodo.feature.setting.settingScreen
import jp.kztproject.rewardedtodo.presentation.reward.list.RewardedTodoScheme

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            MaterialTheme(
                colorScheme = RewardedTodoScheme(isDarkTheme = isSystemInDarkTheme())
            ) {
                val navController = rememberNavController()
                var todoistAuthFinished by remember { mutableStateOf(false) }
                NavHost(navController = navController, startDestination = HOME_SCREEN) {
                    homeScreen(
                        onClickSetting = {
                            navController.navigate(SETTING_SCREEN)
                        },
                    )
                    settingScreen(
                        todoistAuthFinished = todoistAuthFinished,
                    )
                }
            }
        }
    }
}
