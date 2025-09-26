package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken

interface GetApiTokenUseCase {

    suspend fun execute(): ApiToken?
}