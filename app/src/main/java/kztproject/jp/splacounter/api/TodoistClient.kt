package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.model.UserResponse
import javax.inject.Inject

class TodoistClient @Inject
internal constructor(private val service: TodoistService) {

    fun getUser(token: String): Single<UserResponse> {
        return service.getUser(token, "*", "[\"user\"]")
    }

}
