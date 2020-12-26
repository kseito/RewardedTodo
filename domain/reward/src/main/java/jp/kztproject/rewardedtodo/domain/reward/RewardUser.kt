package jp.kztproject.rewardedtodo.domain.reward

//TODO authモジュールにも存在しているので１つにまとめたい
data class RewardUser(val id: Long, val todoistId: Long, val point: Int)