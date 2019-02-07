package kztproject.jp.splacounter.auth.repository

import io.reactivex.Completable
import kztproject.jp.splacounter.api.RewardListClient
import kztproject.jp.splacounter.auth.api.TodoistService
import kztproject.jp.splacounter.model.RewardUser
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class AuthRepository @Inject constructor(private val todoistService: TodoistService,
                                         private val rewardListClient: RewardListClient) {

    fun login(inputString: String): Completable {
        return todoistService.getUser(inputString, "*", "[\"user\"]")
                .flatMap { response: UserResponse -> rewardListClient.findUser(response.user.id.toLong()) }
                .flatMapCompletable { save(it) }
    }

    fun signUp(todoistToken: String): Completable {
        return todoistService.getUser(todoistToken, "*", "[\"user\"]")
                .flatMap { response -> rewardListClient.createUser(response.user.id.toLong()) }
                .flatMapCompletable { save(it) }
    }

    private fun save(user: RewardUser): Completable {
        PrefsWrapper.userId = user.id
        return Completable.complete()
    }
}
