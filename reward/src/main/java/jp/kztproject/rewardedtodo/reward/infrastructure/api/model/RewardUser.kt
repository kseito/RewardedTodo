package jp.kztproject.rewardedtodo.reward.infrastructure.api.model

//TODO authモジュールにも存在しているので１つにまとめたい
data class RewardUser(val id: Long, val todoistId: Long, val point: Int)