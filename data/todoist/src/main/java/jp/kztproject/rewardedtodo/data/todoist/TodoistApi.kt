package jp.kztproject.rewardedtodo.data.todoist

import jp.kztproject.rewardedtodo.data.todoist.model.Task
import jp.kztproject.rewardedtodo.data.todoist.model.TodoistAuthentication
import retrofit2.Response
import retrofit2.http.*

interface TodoistApi {
    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun fetchAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): TodoistAuthentication

    @GET("rest/v2/tasks")
    suspend fun fetchTasks(@Query("filter") filter: String): List<Task>

    @POST("rest/v2/tasks/{todoist_id}/close")
    suspend fun completeTask(@Path("todoist_id") todoistId: Long): Response<Unit>
}
