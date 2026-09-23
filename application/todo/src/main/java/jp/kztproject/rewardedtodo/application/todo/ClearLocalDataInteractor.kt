package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.reward.repository.IAccountCacheRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoRepository
import timber.log.Timber
import javax.inject.Inject

class ClearLocalDataInteractor @Inject constructor(
    private val todoRepository: ITodoRepository,
    private val accountCacheRepository: IAccountCacheRepository,
) : ClearLocalDataUseCase {

    // 片方の失敗でもう片方を飛ばさないよう、削除ごとに結果を受け止める
    override suspend fun execute() {
        runCatching { todoRepository.deleteAll() }
            .onFailure { Timber.e(it, "Failed to delete local todos") }

        runCatching { accountCacheRepository.clear() }
            .onFailure { Timber.e(it, "Failed to clear account cache") }
    }
}
