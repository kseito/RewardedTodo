package jp.kztproject.rewardedtodo.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            var todoistAuthFinished by remember { mutableStateOf(false) }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                todoistAuthFinished = true
            }
            NavHost(navController = navController, startDestination = HOME_SCREEN) {
                homeScreen(
                    onClickSetting = {
                        navController.navigate(SETTING_SCREEN)
                    },
                )
                settingScreen(
                    todoistAuthFinished = todoistAuthFinished,
                    onTodoistAuthStartClicked = {
                        val intent = Intent(context, TodoistAuthActivity::class.java)
                        launcher.launch(intent)
                    },
                )
            }
        }
    }
}
