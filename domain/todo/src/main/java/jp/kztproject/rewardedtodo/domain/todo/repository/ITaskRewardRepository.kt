package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting

interface ITaskRewardRepository {
    suspend fun findAll(): List<TaskRewardSetting>
    suspend fun update(setting: TaskRewardSetting): TaskRewardSetting
}
