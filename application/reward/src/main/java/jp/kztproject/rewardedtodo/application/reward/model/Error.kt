package jp.kztproject.rewardedtodo.application.reward.model

sealed class Error: Throwable() {
    object EmptyTitle: Error()

    object EmptyPoint: Error()

    object EmptyProbability: Error()
}
