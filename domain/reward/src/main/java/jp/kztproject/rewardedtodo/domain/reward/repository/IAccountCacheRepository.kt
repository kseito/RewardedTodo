package jp.kztproject.rewardedtodo.domain.reward.repository

/**
 * 連携しているTodoistアカウントに紐づくローカルキャッシュ。
 *
 * 別のアカウントで連携し直したときに前のアカウントの値を持ち越さないよう、認証の成功時に破棄する。
 */
interface IAccountCacheRepository {

    suspend fun clear()
}
