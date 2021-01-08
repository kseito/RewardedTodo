package jp.kztproject.rewardedtodo.application.reward.model

sealed class Result<V>

data class Success<V>(val value: V) : Result<V>()

data class Failure<V>(val reason: Error) : Result<V>()