package kztproject.jp.splacounter.repository

import io.reactivex.Completable
import kztproject.jp.splacounter.api.RewardListClient
import kztproject.jp.splacounter.api.TodoistClient
import kztproject.jp.splacounter.model.RewardUser
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class AuthRepository @Inject constructor(private val todoistClient: TodoistClient,
                                         private val rewardListClient: RewardListClient) {

    fun login(inputString: String): Completable {
        return todoistClient.getUser(inputString)
                .flatMap { response: UserResponse -> rewardListClient.findUser(response.user.id) }
                .flatMapCompletable { save(it) }
    }

    fun signUp(todoistToken: String): Completable {
        return todoistClient.getUser(todoistToken)
                .flatMap { response -> rewardListClient.createUser(response.user.id.toLong()) }
                .flatMapCompletable { save(it) }
    }

    private fun save(user: RewardUser): Completable {
        PrefsWrapper.userId = user.id
        return Completable.complete()
    }
}
