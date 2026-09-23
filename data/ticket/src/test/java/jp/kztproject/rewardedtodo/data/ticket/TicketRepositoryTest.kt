package jp.kztproject.rewardedtodo.data.ticket

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import jp.kztproject.rewardedtodo.data.ticket.network.model.PointsInfoResponse
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class TicketRepositoryTest {

    private val api = mockk<RewardServerApi>()
    private val userIdRepository = mockk<RewardUserIdRepository>(relaxed = true)
    private val repository = TicketRepository(api, userIdRepository)

    private fun httpException(code: Int) = HttpException(
        Response.error<Unit>(code, "".toResponseBody("application/json".toMediaType())),
    )

    private val points = PointsInfoResponse(
        userId = "user",
        totalPoints = 10,
        availablePoints = 7,
        taskCount = 10,
    )

    @Test
    fun `残数不足の422はLackOfTicketsExceptionに変換される`() = runTest {
        coEvery { userIdRepository.getUserId() } returns "user"
        coEvery { api.consumePoints(any(), any(), any()) } throws httpException(422)

        shouldThrow<LackOfTicketsException> { repository.consumeTickets(3) }
    }

    @Test
    fun `401で再登録して再送したあとの422もLackOfTicketsExceptionに変換される`() = runTest {
        coEvery { userIdRepository.getUserId() } returnsMany listOf("stale-user", "fresh-user")
        coEvery { api.consumePoints("stale-user", any(), any()) } throws httpException(401)
        coEvery { api.consumePoints("fresh-user", any(), any()) } throws httpException(422)

        shouldThrow<LackOfTicketsException> { repository.consumeTickets(3) }

        coVerify(exactly = 1) { userIdRepository.clearUserId() }
    }

    @Test
    fun `401で再登録した再送が成功すればその結果を返す`() = runTest {
        coEvery { userIdRepository.getUserId() } returnsMany listOf("stale-user", "fresh-user")
        coEvery { api.consumePoints("stale-user", any(), any()) } throws httpException(401)
        coEvery { api.consumePoints("fresh-user", any(), any()) } returns points

        repository.consumeTickets(3)

        coVerify(exactly = 1) { api.consumePoints("fresh-user", any(), any()) }
    }

    @Test
    fun `422以外のHTTPエラーはそのまま伝播する`() = runTest {
        coEvery { userIdRepository.getUserId() } returns "user"
        coEvery { api.consumePoints(any(), any(), any()) } throws httpException(500)

        shouldThrow<HttpException> { repository.consumeTickets(3) }.code() shouldBe 500
    }
}
