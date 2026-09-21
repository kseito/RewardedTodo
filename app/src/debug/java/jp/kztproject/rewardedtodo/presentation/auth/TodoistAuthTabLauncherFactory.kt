package jp.kztproject.rewardedtodo.presentation.auth

import androidx.activity.ComponentActivity
import jp.kztproject.rewardedtodo.feature.auth.TodoistAuthTabLauncher

/**
 * 実ブラウザのAuth Tabで認可を行う。
 */
fun createTodoistAuthTabLauncher(activity: ComponentActivity): TodoistAuthTabLauncher =
    AuthTabTodoistAuthTabLauncher(activity)
