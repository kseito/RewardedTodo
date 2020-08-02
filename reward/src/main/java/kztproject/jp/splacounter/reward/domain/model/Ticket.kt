package kztproject.jp.splacounter.reward.domain.model

sealed class Ticket {
    class Prize(val rewardId: Int) : Ticket()

    object Miss : Ticket()
}