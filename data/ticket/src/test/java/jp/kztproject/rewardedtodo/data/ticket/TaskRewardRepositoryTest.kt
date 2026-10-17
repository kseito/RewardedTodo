package jp.kztproject.rewardedtodo.data.ticket

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.*
import jp.kztproject.rewardedtodo.domain.todo.TaskRewardSetting
import jp.kztproject.rewardedtodo.domain.todo.TodoistTaskId
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class TaskRewardRepositoryTest {
    private val api = mockk<RewardServerApi>()
    private val users = mockk<RewardUserIdRepository>(relaxed = true)
    private val repository = TaskRewardRepository(api, users)

    @Test
    fun `returns server settings and retries authentication only once`() = runTest {
        coEvery { users.getUserId() } returns "user"
        coEvery { users.getToken() } returns "token"
        coEvery { api.getTaskRewards("user", "Bearer token") } throws
            HttpException(Response.error<Unit>(401, "".toResponseBody())) andThen
            listOf(TaskRewardResponse("task", 3))
        repository.findAll() shouldBe listOf(TaskRewardSetting(TodoistTaskId("task"), 3))
        coVerify(exactly = 1) { users.clearUserId() }
    }

    @Test
    fun `update propagates network failure`() = runTest {
        coEvery { api.updateTaskReward(any(), any(), any(), any()) } throws
            HttpException(Response.error<Unit>(500, "".toResponseBody()))
        shouldThrow<HttpException> { repository.update(TaskRewardSetting(TodoistTaskId("task"), 3)) }
    }
}
