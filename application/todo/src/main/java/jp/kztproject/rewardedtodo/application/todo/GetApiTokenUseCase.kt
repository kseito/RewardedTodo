package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import kotlinx.coroutines.flow.Flow

interface GetApiTokenUseCase {

    suspend fun execute(): ApiToken?

    fun executeAsFlow(): Flow<ApiToken?>
}
