package jp.kztproject.rewardedtodo.presentation.auth

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.auth.AuthTabIntent
import androidx.browser.customtabs.CustomTabsClient
import androidx.core.net.toUri
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabLauncher
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Chrome Custom TabsのAuth Tabで[TodoistAuthTabLauncher]を実装する。
 *
 * リダイレクト先はHTTPS（Todoistがカスタムスキームを受け付けないため）で、
 * ブラウザがDigital Asset Linksを検証してからアプリに結果を返す。
 *
 * @param activity Auth Tabを起動するActivity。[ActivityResultLauncher]の登録がSTARTED以降だと
 *   例外になるため、Activityのフィールド初期化時に生成すること。
 */
class AuthTabTodoistAuthTabLauncher(private val activity: ComponentActivity) : TodoistAuthTabLauncher {

    // 画面がリダイレクト待ちの間に再生成されても結果を取りこぼさないよう、
    // 購読されるまでバッファするChannelで一度だけ配送する
    private val resultChannel = Channel<TodoistAuthTabResult>(Channel.BUFFERED)

    override val results: Flow<TodoistAuthTabResult> = resultChannel.receiveAsFlow()

    private val launcher: ActivityResultLauncher<Intent> =
        AuthTabIntent.registerActivityResultLauncher(activity) { authResult ->
            resultChannel.trySend(authResult.toTodoistAuthTabResult())
        }

    override fun isSupported(): Boolean {
        val browserPackage = CustomTabsClient.getPackageName(activity, null) ?: return false
        return CustomTabsClient.isAuthTabSupported(activity, browserPackage)
    }

    override fun launch(authorizeUrl: String) {
        AuthTabIntent.Builder().build().launch(
            launcher,
            authorizeUrl.toUri(),
            BuildConfig.TODOIST_REDIRECT_HOST,
            BuildConfig.TODOIST_REDIRECT_PATH,
        )
    }

    private fun AuthTabIntent.AuthResult.toTodoistAuthTabResult(): TodoistAuthTabResult = when (resultCode) {
        AuthTabIntent.RESULT_OK ->
            resultUri?.let { TodoistAuthTabResult.Succeeded(it.toString()) }
                ?: TodoistAuthTabResult.VerificationFailed

        AuthTabIntent.RESULT_CANCELED -> TodoistAuthTabResult.Canceled

        // RESULT_VERIFICATION_FAILED / RESULT_VERIFICATION_TIMED_OUT / RESULT_UNKNOWN_CODE
        else -> TodoistAuthTabResult.VerificationFailed
    }
}
