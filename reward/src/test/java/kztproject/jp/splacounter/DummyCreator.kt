package kztproject.jp.splacounter

import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardPoint

object DummyCreator {

    fun createDummyRewardUser(): RewardUser {
        return RewardUser(10, 123, 0)
    }

    fun createDummyReward(): RewardEntity {
        return RewardEntity(1, "Test", 5, 10F, "Test description", true)
    }

    fun createDummyNoRepeatReward(): RewardEntity {
        return RewardEntity(2, "Test2", 7, 20F, "Test description", false)
    }

    fun createDummyRewardPoint(): RewardPoint {
        return RewardPoint(10)
    }
}
