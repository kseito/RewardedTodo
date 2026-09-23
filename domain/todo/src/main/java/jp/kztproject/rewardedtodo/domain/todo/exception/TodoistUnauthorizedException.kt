package jp.kztproject.rewardedtodo.domain.todo.exception

import java.io.IOException

/**
 * Todoistのアクセストークンが取得できず、リクエストを送れなかった。
 *
 * 有効なトークンが無い状態でTodoist APIを呼び出したときに、OkHttpのInterceptorから投げられる。
 * Interceptor内で投げる例外はRetrofitが[IOException]として扱えないと伝播が不安定になるため、
 * [IOException]を継承している。
 */
class TodoistUnauthorizedException : IOException("Todoist access token is unavailable")
