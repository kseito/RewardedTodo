package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.*
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import jp.kztproject.rewardedtodo.domain.todo.repository.ITaskRewardRepository
import retrofit2.HttpException
import javax.inject.Inject

class TaskRewardRepository @Inject internal constructor(
    private val api: RewardServerApi,
    private val users: RewardUserIdRepository,
) : ITaskRewardRepository {
    override suspend fun findAll(): List<TaskRewardSetting> = authenticated { user, token ->
        api.getTaskRewards(user, token).map { it.toDomain() }
    }

    override suspend fun update(setting: TaskRewardSetting): TaskRewardSetting {
        require(setting.points in 1..100)
        return authenticated { user, token ->
            api.updateTaskReward(
                user,
                setting.taskId.value,
                token,
                UpdateTaskRewardRequest(setting.points),
            ).toDomain()
        }
    }

    override suspend fun import(settings: List<TaskRewardSetting>) {
        settings.chunked(100).forEach { batch ->
            authenticated { user, token ->
                api.importTaskRewards(
                    user,
                    token,
                    ImportTaskRewardsRequest(
                        batch.map {
                            ImportTaskReward(it.taskId.value, it.points)
                        },
                    ),
                )
            }
        }
    }

    private suspend fun <T> authenticated(block: suspend (String, String) -> T): T = try {
        block(users.getUserId(), "Bearer ${users.getToken()}")
    } catch (e: HttpException) {
        if (e.code() != 401) throw e
        users.clearUserId()
        block(users.getUserId(), "Bearer ${users.getToken()}")
    }

    private fun TaskRewardResponse.toDomain() = TaskRewardSetting(TodoistTaskId(taskId), points)
}
