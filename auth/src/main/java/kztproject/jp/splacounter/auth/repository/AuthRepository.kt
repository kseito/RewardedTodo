package kztproject.jp.splacounter.auth.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.auth.api.model.RewardUser
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class AuthRepository @Inject
constructor(private val todoistService: TodoistService,
            private val rewardListClient: RewardListLoginService,
            private val prefsWrapper: PrefsWrapper) : IAuthRepository {

    override suspend fun login(inputString: String) {
        val response = todoistService.getUser(inputString, "*", "[\"user\"]").await()
        val user = rewardListClient.findUser(response.user.id.toLong()).await()
        save(user)
    }

    override suspend fun signUp(todoistToken: String) {
        return withContext(Dispatchers.IO) {
            val response = todoistService.getUser(todoistToken, "*", "[\"user\"]").await()
            val user = rewardListClient.createUser(response.user.id.toLong()).await()
            save(user)
        }
    }

    private fun save(user: RewardUser) {
        prefsWrapper.userId = user.id
    }
}
