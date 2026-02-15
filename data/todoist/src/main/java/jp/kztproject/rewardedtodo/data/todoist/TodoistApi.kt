package jp.kztproject.rewardedtodo.data.todoist

import jp.kztproject.rewardedtodo.data.todoist.model.Tasks
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

    @GET("api/v1/tasks/filter")
    suspend fun fetchTasks(@Query("query") filter: String): Tasks

    @POST("api/v1/tasks/{todoist_id}/close")
    suspend fun completeTask(@Path("todoist_id") todoistId: String): Response<Unit>
}
