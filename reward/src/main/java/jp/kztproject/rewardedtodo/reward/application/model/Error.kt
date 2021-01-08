package jp.kztproject.rewardedtodo.reward.application.model

sealed class Error {
    object EmptyTitle: Error()

    object EmptyPoint: Error()

    object EmptyProbability: Error()
}
