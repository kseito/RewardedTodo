package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardServerApi
import jp.kztproject.rewardedtodo.common.rewardserver.network.model.*
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
    fun `returns server settings`() = runTest {
        coEvery { users.getUserId() } returns "user"
        coEvery { api.getTaskRewards("user") } returns listOf(TaskRewardResponse("task", 3))
        repository.findAll() shouldBe listOf(TaskRewardSetting(TodoistTaskId("task"), 3))
    }

    @Test
    fun `update propagates network failure`() = runTest {
        coEvery { api.updateTaskReward(any(), any(), any()) } throws
            HttpException(Response.error<Unit>(500, "".toResponseBody()))
        shouldThrow<HttpException> { repository.update(TaskRewardSetting(TodoistTaskId("task"), 3)) }
    }
}
