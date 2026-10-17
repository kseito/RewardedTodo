package jp.kztproject.rewardedtodo.domain.todo

data class TaskRewardSetting(val taskId: TodoistTaskId, val points: Int, val version: Int = 0)
