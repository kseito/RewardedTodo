package kztproject.jp.splacounter.auth.repository

import io.reactivex.Completable
import kztproject.jp.splacounter.auth.api.RewardListLoginService
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.auth.api.model.UserResponse
import kztproject.jp.splacounter.auth.preference.PrefsWrapper
import javax.inject.Inject

class AuthRepository @Inject
constructor(private val todoistService: TodoistService,
            private val rewardListClient: RewardListLoginService,
            private val prefsWrapper: PrefsWrapper) : IAuthRepository {

    override fun login(inputString: String): Completable {
        return todoistService.getUser(inputString, "*", "[\"user\"]")
                .flatMap { response: UserResponse -> rewardListClient.findUser(response.user.id.toLong()) }
                .flatMapCompletable { save(it) }
    }

    override fun signUp(todoistToken: String): Completable {
        return todoistService.getUser(todoistToken, "*", "[\"user\"]")
                .flatMap { response -> rewardListClient.createUser(response.user.id.toLong()) }
                .flatMapCompletable { save(it) }
    }

    private fun save(user: RewardUser): Completable {
        prefsWrapper.userId = user.id
        return Completable.complete()
    }
}
