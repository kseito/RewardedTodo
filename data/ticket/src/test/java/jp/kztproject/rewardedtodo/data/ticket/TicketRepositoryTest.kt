package jp.kztproject.rewardedtodo.data.ticket

import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardServerApi
import jp.kztproject.rewardedtodo.common.rewardserver.network.model.PointsResponse
import kotlinx.coroutines.flow.first
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

    private val points = PointsResponse(
        userId = "user",
        totalPoints = 10,
        availablePoints = 7,
        taskCount = 10,
    )

    @Test
    fun `サーバーの残数を返す`() = runTest {
        coEvery { userIdRepository.getUserId() } returns "user"
        coEvery { api.getPoints("user") } returns points

        repository.getNumberOfTicket().first() shouldBe 7
    }

    @Test
    fun `HTTPエラーはそのまま伝播する`() = runTest {
        coEvery { userIdRepository.getUserId() } returns "user"
        coEvery { api.getPoints(any()) } throws httpException(500)

        shouldThrow<HttpException> { repository.getNumberOfTicket().first() }.code() shouldBe 500
    }
}
