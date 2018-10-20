package kztproject.jp.splacounter

import kztproject.jp.splacounter.database.model.Reward
import kztproject.jp.splacounter.model.Counter
import kztproject.jp.splacounter.model.RewardUser
import kztproject.jp.splacounter.model.TodoistUser
import kztproject.jp.splacounter.model.UserResponse

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

    fun createDummyCounter(): Counter {
        val counter = Counter()
        counter.id = 1
        counter.count = 10
        return counter
    }

    fun createDummyReward(): Reward {
        return Reward(1, "Test", 5, "Test description", true)
    }

    fun createDummyNoRepeatReward(): Reward {
        return Reward(2, "Test2", 7, "Test description", false)
    }
}
