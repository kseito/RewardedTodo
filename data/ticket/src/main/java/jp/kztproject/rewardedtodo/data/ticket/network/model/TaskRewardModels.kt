package jp.kztproject.rewardedtodo.data.ticket.network.model

import com.squareup.moshi.Json

data class TaskRewardResponse(
    @Json(name = "todoist_task_id") val taskId: String,
    @Json(name = "points_per_completion") val points: Int,
)

data class UpdateTaskRewardRequest(@Json(name = "points_per_completion") val points: Int)
