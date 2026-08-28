package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

@ExperimentalCoroutinesApi
class TodoistCredentialRepositoryTest {

    private lateinit var directory: File
    private lateinit var scope: TestScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: TodoistCredentialRepository

    @Before
    fun setup() {
        directory = Files.createTempDirectory("credential-store").toFile()
        scope = TestScope(UnconfinedTestDispatcher())
        dataStore = PreferenceDataStoreFactory.create(scope = scope) { File(directory, "test.preferences_pb") }
        repository = TodoistCredentialRepository(dataStore)
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun `getCredential returns null before anything is saved`() = runTest {
        repository.getCredential() shouldBe null
    }

    @Test
    fun `saveCredential round-trips the access token, refresh token and expiry`() = runTest {
        val credential = TodoistCredential(
            accessToken = ApiToken.create("access-token"),
            refreshToken = RefreshToken.create("refresh-token"),
            expiresAt = 1_700_000_000_000L,
        )

        repository.saveCredential(credential)

        val stored = repository.getCredential().shouldNotBeNull()
        stored shouldBe credential
    }

    @Test
    fun `saveCredential clears a previously stored refresh token when the new one has none`() = runTest {
        repository.saveCredential(
            TodoistCredential(ApiToken.create("access-1"), RefreshToken.create("refresh-1"), expiresAt = 1_000L),
        )

        repository.saveCredential(TodoistCredential(ApiToken.create("access-2")))

        val stored = repository.getCredential().shouldNotBeNull()
        stored.refreshToken shouldBe null
        stored.expiresAt shouldBe null
    }

    @Test
    fun `deleteCredential removes every stored field`() = runTest {
        repository.saveCredential(
            TodoistCredential(ApiToken.create("access"), RefreshToken.create("refresh"), expiresAt = 1_000L),
        )

        repository.deleteCredential()

        repository.getCredential() shouldBe null
    }

    @Test
    fun `getCredentialAsFlow emits the stored credential`() = runTest {
        repository.saveCredential(TodoistCredential(ApiToken.create("access")))

        repository.getCredentialAsFlow().first().shouldNotBeNull().accessToken.value shouldBe "access"
    }
}
