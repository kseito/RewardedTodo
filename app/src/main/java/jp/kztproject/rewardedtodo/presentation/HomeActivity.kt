package jp.kztproject.rewardedtodo.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.kztproject.rewardedtodo.feature.auth.TodoistAuthActivity
import jp.kztproject.rewardedtodo.feature.setting.SETTING_SCREEN
import jp.kztproject.rewardedtodo.feature.setting.settingScreen

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                // No action needed here.
            }
            NavHost(navController = navController, startDestination = HOME_SCREEN) {
                homeScreen(
                    onClickSetting = {
                        navController.navigate(SETTING_SCREEN)
                    },
                )
                settingScreen(
                    onTodoistAuthStartClicked = {
                        val intent = Intent(context, TodoistAuthActivity::class.java)
                        launcher.launch(intent)
                    },
                )
            }
        }
    }
}
