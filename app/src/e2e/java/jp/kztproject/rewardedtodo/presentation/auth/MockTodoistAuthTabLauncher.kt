package jp.kztproject.rewardedtodo.presentation.auth

import androidx.core.net.toUri
import jp.kztproject.rewardedtodo.BuildConfig
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabLauncher
import jp.kztproject.rewardedtodo.feature.setting.TodoistAuthTabResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * E2E用に、ブラウザを介さず認可が成功したことにする[TodoistAuthTabLauncher]。
 *
 * CIのエミュレータにはAuth Tabが要求するChrome 137+が存在しないため、実ブラウザでは
 * 認可を完走できない。代わりに認可URLから`state`を取り出してリダイレクトURIを組み立て、
 * 即座に成功として返す。以降のトークン交換はフェイクバックエンドが応答する。
 */
class MockTodoistAuthTabLauncher : TodoistAuthTabLauncher {

    private val resultChannel = Channel<TodoistAuthTabResult>(Channel.BUFFERED)

    override val results: Flow<TodoistAuthTabResult> = resultChannel.receiveAsFlow()

    override fun isSupported(): Boolean = true

    override fun launch(authorizeUrl: String) {
        // stateを echo しないと CompleteTodoistAuthInteractor の検証で弾かれる
        val state = authorizeUrl.toUri().getQueryParameter("state").orEmpty()
        val redirectUri = "${BuildConfig.TODOIST_REDIRECT_URI}?code=$MOCK_AUTHORIZATION_CODE&state=$state"
        resultChannel.trySend(TodoistAuthTabResult.Succeeded(redirectUri))
    }

    private companion object {
        const val MOCK_AUTHORIZATION_CODE = "mock-authorization-code"
    }
}
