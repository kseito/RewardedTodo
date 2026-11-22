package jp.kztproject.rewardedtodo.data.reward.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class LotteryAnimationPreferenceRepositoryTest {
    @Test
    fun `未設定時は演出が有効になっている`() = runTest {
        val repository = createRepository()

        repository.observeEnabled().first() shouldBe true
    }

    @Test
    fun `設定した演出の有効状態を読み出せる`() = runTest {
        val repository = createRepository()

        repository.setEnabled(false)

        repository.observeEnabled().first() shouldBe false
    }

    private fun createRepository(): LotteryAnimationPreferenceRepository {
        val directory = createTempDirectory().toFile()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            File(directory, "preferences.preferences_pb")
        }
        return LotteryAnimationPreferenceRepository(dataStore)
    }
}
