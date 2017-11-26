package kztproject.jp.splacounter.repository

import io.reactivex.Completable
import kztproject.jp.splacounter.api.TodoistClient
import kztproject.jp.splacounter.model.UserResponse
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class AuthRepository @Inject constructor(private val client: TodoistClient) {

    fun login(inputString: String): Completable {
        return client.getUser(inputString)
                .flatMapCompletable({ this.save(it) })
    }

    private fun save(response: UserResponse): Completable {
        val user = response.user
        PrefsWrapper.userId = user.id
        PrefsWrapper.userName = user.fullName
        return Completable.complete()
    }
}
