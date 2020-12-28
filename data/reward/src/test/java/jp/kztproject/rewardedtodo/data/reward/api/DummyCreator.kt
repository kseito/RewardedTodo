package jp.kztproject.rewardedtodo.data.reward.api

import jp.kztproject.rewardedtodo.data.reward.api.model.NumberOfTicket
import jp.kztproject.rewardedtodo.domain.reward.RewardUser


object DummyCreator {
    fun createDummyRewardPoint(): NumberOfTicket {
        return NumberOfTicket(10)
    }

    fun createDummyRewardUser(): RewardUser {
        return RewardUser(10, 123, 0)
    }

}