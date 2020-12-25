package jp.kztproject.rewardedtodo.domain.reward

sealed class Ticket {
    companion object {
        const val ISSUE_LIMIT = 10000
    }

    class Prize(val rewardId: RewardId) : Ticket()

    object Miss : Ticket()
}