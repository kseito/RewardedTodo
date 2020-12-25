package jp.kztproject.rewardedtodo.auth

import jp.kztproject.rewardedtodo.auth.api.model.RewardUser
import jp.kztproject.rewardedtodo.auth.api.model.TodoistUser
import jp.kztproject.rewardedtodo.auth.api.model.UserResponse

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