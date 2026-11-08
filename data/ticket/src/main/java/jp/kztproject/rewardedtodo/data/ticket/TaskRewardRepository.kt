package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardServerApi
import jp.kztproject.rewardedtodo.common.rewardserver.network.model.*
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import javax.inject.Inject

class TaskRewardRepository @Inject internal constructor(
    private val api: RewardServerApi,
    private val users: RewardUserIdRepository,
) : ITaskRewardRepository {
    override suspend fun findAll(): List<TaskRewardSetting> =
        api.getTaskRewards(users.getUserId()).map { it.toDomain() }

    override suspend fun update(setting: TaskRewardSetting): TaskRewardSetting {
        require(setting.points in 1..100)
        return api.updateTaskReward(
            users.getUserId(),
            setting.taskId.value,
            UpdateTaskRewardRequest(setting.points),
        ).toDomain()
    }

    private fun TaskRewardResponse.toDomain() = TaskRewardSetting(TodoistTaskId(taskId), points)
}
