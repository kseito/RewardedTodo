package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTodoistTokenInteractor @Inject constructor(
    private val authRepository: ITodoistAuthRepository,
    private val credentialRepository: ITodoistCredentialRepository,
) : RefreshTodoistTokenUseCase {

    // 複数リクエストが同時に401を受けた際にリフレッシュが並走すると、ローテーションされた
    // リフレッシュトークンを互いに無効化してしまうため直列化する
    private val mutex = Mutex()

    override suspend fun execute(): Result<ApiToken> {
        // ロック取得前のトークンを控えておく。ロック待ちの間に別のリクエストが更新していれば
        // ロック取得後には値が変わっているので、通信せずその結果を共有できる
        val observed = credentialRepository.getCredential()
            ?: return Result.failure(TokenError.NotConnected())

        return mutex.withLock { refreshUnless(observed.accessToken) }
    }

    /**
     * 保存済みトークンが[observedToken]から変わっていなければリフレッシュする。
     *
     * 有効期限ではなくトークンの同一性で判断するのが要点。401は「ローカルの期限内でも
     * サーバーがそのトークンを拒否した」という意味なので、期限で早期returnすると
     * 呼び出し元へ同じトークンを返してしまい、リクエストの再送が止まってしまう。
     */
    private suspend fun refreshUnless(observedToken: ApiToken): Result<ApiToken> {
        val current = credentialRepository.getCredential()
            ?: return Result.failure(TokenError.NotConnected())

        val refreshToken = current.refreshToken
        return when {
            current.accessToken != observedToken -> Result.success(current.accessToken)
            refreshToken == null -> Result.failure(TokenError.RefreshFailed(TokenError.NotConnected()))
            else -> refresh(current, refreshToken)
        }
    }

    private suspend fun refresh(current: TodoistCredential, refreshToken: RefreshToken): Result<ApiToken> =
        authRepository.refreshCredential(refreshToken)
            .mapCatching { refreshed ->
                // 60秒のグレース期間内の再試行ではrefresh_tokenが返らないため、既存の値を引き継ぐ
                val merged = current.merge(refreshed)
                credentialRepository.saveCredential(merged)
                merged.accessToken
            }
            .recoverCatching { cause ->
                throw if (cause is TokenError) cause else TokenError.RefreshFailed(cause)
            }
}
