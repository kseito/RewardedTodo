package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.OAuthState
import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

@ExperimentalCoroutinesApi
class TodoistAuthSessionRepositoryTest {

    private lateinit var directory: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: TodoistAuthSessionRepository

    @Before
    fun setup() {
        directory = Files.createTempDirectory("auth-session-store").toFile()
        val scope = TestScope(UnconfinedTestDispatcher())
        dataStore = PreferenceDataStoreFactory.create(scope = scope) { File(directory, "test.preferences_pb") }
        repository = TodoistAuthSessionRepository(dataStore)
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun `getSession returns null when no authorization is pending`() = runTest {
        repository.getSession() shouldBe null
    }

    @Test
    fun `saveSession round-trips the state and code verifier`() = runTest {
        val session = TodoistAuthSession(
            state = OAuthState.create("issued-state"),
            codeVerifier = CodeVerifier.generate(),
        )

        repository.saveSession(session)

        repository.getSession().shouldNotBeNull() shouldBe session
    }

    @Test
    fun `clearSession discards the pending authorization`() = runTest {
        repository.saveSession(
            TodoistAuthSession(OAuthState.create("issued-state"), CodeVerifier.generate()),
        )

        repository.clearSession()

        repository.getSession() shouldBe null
    }
}
