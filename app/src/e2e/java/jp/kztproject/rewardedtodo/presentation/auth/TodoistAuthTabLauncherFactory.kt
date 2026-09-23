package jp.kztproject.rewardedtodo.presentation.auth

import androidx.activity.ComponentActivity
import jp.kztproject.rewardedtodo.feature.auth.TodoistAuthTabLauncher

/**
 * E2Eではブラウザを介さずに認可を完了させる。
 *
 * 実装は e2e ビルドタイプにしか存在しないため、debug / release のAPKには含まれない。
 */
fun createTodoistAuthTabLauncher(activity: ComponentActivity): TodoistAuthTabLauncher = MockTodoistAuthTabLauncher()
