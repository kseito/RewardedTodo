package kztproject.jp.splacounter.auth

import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.auth.api.model.TodoistUser
import kztproject.jp.splacounter.auth.api.model.UserResponse

object DummyCreator {

    fun createDummyUserResponse(): UserResponse {
        val user = TodoistUser()
        user.id = 1
        user.fullName = "test_user"
        return UserResponse(user)
    }

    fun createDummyRewardUser(): RewardUser {
        return RewardUser(10, 123, 0)
    }
}