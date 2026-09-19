package jp.kztproject.rewardedtodo.presentation.auth

import androidx.activity.ComponentActivity
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabLauncher

/**
 * `-PuseMockServer=true` でビルドしたときだけ、ブラウザを介さないE2E用の実装に差し替える。
 * 通常のdebugビルドは実ブラウザのAuth Tabを使う。
 */
fun createTodoistAuthTabLauncher(activity: ComponentActivity): TodoistAuthTabLauncher =
    if (BuildConfig.USE_MOCK_SERVER) {
        MockTodoistAuthTabLauncher()
    } else {
        AuthTabTodoistAuthTabLauncher(activity)
    }
