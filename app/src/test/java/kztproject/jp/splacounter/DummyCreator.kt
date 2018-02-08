package kztproject.jp.splacounter

import kztproject.jp.splacounter.model.Counter
import kztproject.jp.splacounter.model.Reward
import kztproject.jp.splacounter.model.User
import kztproject.jp.splacounter.model.UserResponse

object DummyCreator {

    fun createDummyUserResponse(): UserResponse {
        val response = UserResponse()
        val user = User()
        user.id = 1
        user.fullName = "test_user"
        response.user = user
        return response
    }

    fun createDummyCounter(): Counter {
        val counter = Counter()
        counter.id = 1
        counter.count = 10
        return counter
    }

    fun createDummyReward(): Reward {
        return Reward(1, "Test", 5, "Test description", "")
    }
}
