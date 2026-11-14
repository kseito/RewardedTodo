package jp.kztproject.rewardedtodo.data.reward.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.*
import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.data.reward.network.*
import jp.kztproject.rewardedtodo.domain.reward.exception.PendingLotteryException
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files

class LotteryRepositoryTest {
    @Test fun `lost response survives repository recreation and resends same request`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val directory = Files.createTempDirectory("lottery-test").toFile()
        try {
            val store = PreferenceDataStoreFactory.create(scope = scope) { File(directory, "test.preferences_pb") }
            val api = mockk<RewardApi>()
            var user = "first"
            val users = mockk<RewardUserIdRepository> { coEvery { getUserId() } coAnswers { user } }
            val requests = mutableListOf<LotteryRequest>()
            coEvery { api.draw("first", capture(requests)) } throws IOException("lost response")
            val first = LotteryRepository(api, users, store)
            shouldThrow<IOException> { first.draw(10) }
            shouldThrow<PendingLotteryException> { first.draw(1) }
            val recreated = LotteryRepository(api, users, store)
            user = "second"
            recreated.recoverPending() shouldBe null
            coVerify(exactly = 0) { api.draw("second", any()) }
            user = "first"
            coEvery { api.draw("first", capture(requests)) } returns
                LotteryResponse(10, List(10) { LotteryDrawResponse("miss", null) })
            recreated.recoverPending()!!.missCount shouldBe 10
            requests.size shouldBe 2
            requests[0] shouldBe requests[1]
            recreated.recoverPending() shouldBe null
        } finally {
            scope.cancel()
            directory.deleteRecursively()
        }
    }
}
