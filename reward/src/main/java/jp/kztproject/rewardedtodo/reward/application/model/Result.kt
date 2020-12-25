package jp.kztproject.rewardedtodo.reward.application.model

import jp.kztproject.rewardedtodo.reward.application.CommonException

sealed class Result<V>

data class Success<V>(val value: V) : Result<V>()

data class Failure<V>(val reason: CommonException) : Result<V>()