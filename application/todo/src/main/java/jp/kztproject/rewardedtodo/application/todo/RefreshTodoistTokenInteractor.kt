package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
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
    private val currentTimeProvider: CurrentTimeProvider,
) : RefreshTodoistTokenUseCase {

    // 複数リクエストが同時に401を受けた際にリフレッシュが並走すると、ローテーションされた
    // リフレッシュトークンを互いに無効化してしまうため直列化する
    private val mutex = Mutex()

    override suspend fun execute(): Result<ApiToken> = mutex.withLock {
        val current = credentialRepository.getCredential()
            ?: return@withLock Result.failure(TokenError.NotConnected())

        // ロック待ちの間に別のリクエストがリフレッシュを済ませていたらそれを使う
        if (!current.isExpired(currentTimeProvider.nowMillis())) {
            return@withLock Result.success(current.accessToken)
        }

        val refreshToken = current.refreshToken
            ?: return@withLock Result.failure(TokenError.RefreshFailed(TokenError.NotConnected()))

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
}
