package jp.kztproject.rewardedtodo.feature.setting

import kotlinx.coroutines.flow.Flow

/**
 * Todoistの認可画面をブラウザで開き、リダイレクト結果を返す。
 *
 * 実装（Chrome Custom TabsのAuth Tab）はappモジュールに置く。feature層が`androidx.browser`へ
 * 直接依存しないよう、境界をこのインターフェースで切っている。
 */
interface TodoistAuthTabLauncher {

    /** 認可の結果。[launch]の呼び出しごとに1件流れる。 */
    val results: Flow<TodoistAuthTabResult>

    /** 端末のブラウザがAuth Tabに対応しているか（Chrome 137以降が必要）。 */
    fun isSupported(): Boolean

    fun launch(authorizeUrl: String)
}

sealed interface TodoistAuthTabResult {

    /** 認可が完了し、リダイレクトURIが返ってきた。codeとstateの検証はこの後で行う。 */
    data class Succeeded(val redirectUri: String) : TodoistAuthTabResult

    /** ユーザーがAuth Tabを閉じた。 */
    data object Canceled : TodoistAuthTabResult

    /** リダイレクト先のDigital Asset Links検証に失敗した、またはタイムアウトした。 */
    data object VerificationFailed : TodoistAuthTabResult
}
