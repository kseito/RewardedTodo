package jp.kztproject.rewardedtodo.data.todoist.model

data class Task(
    val id: Long,
    val content: String,
    val completed: Boolean,
    val due: Due
)
