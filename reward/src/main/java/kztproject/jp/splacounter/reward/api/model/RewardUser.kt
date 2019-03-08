package kztproject.jp.splacounter.reward.api.model

//TODO authモジュールにも存在しているので１つにまとめたい
data class RewardUser(val id: Long, val todoistId: Long, val point: Int)