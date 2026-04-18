package jp.kztproject.rewardedtodo.data.todoist

import jp.kztproject.rewardedtodo.data.todoist.model.Tasks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoistApi {
    @GET("api/v1/tasks/filter")
    suspend fun fetchTasks(@Query("query") filter: String): Tasks

    @POST("api/v1/tasks/{todoist_id}/close")
    suspend fun completeTask(@Path("todoist_id") todoistId: String): Response<Unit>
}
