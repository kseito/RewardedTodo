package kztproject.jp.splacounter

import kztproject.jp.splacounter.auth.api.model.RewardUser
import kztproject.jp.splacounter.auth.api.model.TodoistUser
import kztproject.jp.splacounter.auth.api.model.UserResponse
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.database.model.RewardPoint

object DummyCreator {

    fun createDummyUserResponse(): UserResponse {
        val response = UserResponse()
        val user = TodoistUser()
        user.id = 1
        user.fullName = "test_user"
        response.user = user
        return response
    }

    fun createDummyRewardUser(): RewardUser {
        return RewardUser(10, 123, 0)
    }

    fun createDummyReward(): Reward {
        return Reward(1, "Test", 5, "Test description", true)
    }

    fun createDummyNoRepeatReward(): Reward {
        return Reward(2, "Test2", 7, "Test description", false)
    }

    fun createDummyRewardPoint(): RewardPoint {
        return RewardPoint(10)
    }
}
