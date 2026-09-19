package jp.kztproject.rewardedtodo.presentation.auth

import androidx.activity.ComponentActivity
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabLauncher

/**
 * 実ブラウザのAuth Tabで認可を行う。
 *
 * `-PuseMockServer=true` を付けずにビルドした場合はこちらが使われる。
 */
fun createTodoistAuthTabLauncher(activity: ComponentActivity): TodoistAuthTabLauncher =
    AuthTabTodoistAuthTabLauncher(activity)
