package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import javax.inject.Inject

class GetApiTokenInteractor @Inject constructor(private val apiTokenRepository: IApiTokenRepository) :
    GetApiTokenUseCase {

    override suspend fun execute(): ApiToken? = apiTokenRepository.getToken()
}
