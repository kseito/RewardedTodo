package jp.kztproject.rewardedtodo.domain.todo

/**
 * Todoist OAuthのクライアント設定。
 *
 * 値はビルド設定(BuildConfig)由来だが、application層とdata層の双方が参照するため、
 * 双方が依存できる唯一の場所であるdomain層に置いている
 * （`docs/module-dependency.md` の依存方向ルールによる）。
 *
 * @param clientId OAuth Client ID Metadata DocumentのURL。公開クライアントのためclient_secretは持たない
 * @param authorizeUrl 認可画面のURL
 * @param redirectUri 認可後の戻り先。TodoistはHTTPSのみ受け付ける
 * @param scope 要求する権限（カンマ区切り）
 */
data class TodoistOAuthConfig(
    val clientId: String,
    val authorizeUrl: String,
    val redirectUri: String,
    val scope: String,
)
