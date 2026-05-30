package jp.kztproject.rewardedtodo.data.ticket

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class RoutingTicketRepositoryTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testScope = TestScope()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var localRepository: LocalTicketRepository
    private lateinit var networkRepository: NetworkTicketRepository
    private lateinit var repository: RoutingTicketRepository

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_routing.preferences_pb") },
        )
        localRepository = mockk(relaxed = true)
        networkRepository = mockk(relaxed = true)
        repository = RoutingTicketRepository(localRepository, networkRepository, dataStore)
    }

    private suspend fun setToken(token: String?) {
        dataStore.edit { settings ->
            if (token == null) {
                settings.remove(UserPreferencesKeys.TODOIST_API_TOKEN)
            } else {
                settings[UserPreferencesKeys.TODOIST_API_TOKEN] = token
            }
        }
    }

    @Test
    fun `addTicket delegates to local when token is missing`() = testScope.runTest {
        repository.addTicket(3)

        coVerify(exactly = 1) { localRepository.addTicket(3) }
        coVerify(exactly = 0) { networkRepository.addTicket(any()) }
    }

    @Test
    fun `addTicket delegates to network when token is present`() = testScope.runTest {
        setToken("dummy-token")

        repository.addTicket(2)

        coVerify(exactly = 1) { networkRepository.addTicket(2) }
        coVerify(exactly = 0) { localRepository.addTicket(any()) }
    }

    @Test
    fun `addTicket treats blank token as not connected`() = testScope.runTest {
        setToken("   ")

        repository.addTicket(1)

        coVerify(exactly = 1) { localRepository.addTicket(1) }
        coVerify(exactly = 0) { networkRepository.addTicket(any()) }
    }

    @Test
    fun `consumeTicket delegates by token presence`() = testScope.runTest {
        repository.consumeTicket()
        coVerify(exactly = 1) { localRepository.consumeTicket() }

        setToken("dummy-token")
        repository.consumeTicket()
        coVerify(exactly = 1) { networkRepository.consumeTicket() }
    }

    @Test
    fun `consumeTickets delegates by token presence`() = testScope.runTest {
        repository.consumeTickets(5)
        coVerify(exactly = 1) { localRepository.consumeTickets(5) }

        setToken("dummy-token")
        repository.consumeTickets(7)
        coVerify(exactly = 1) { networkRepository.consumeTickets(7) }
    }

    @Test
    fun `getNumberOfTicket returns local flow when not connected`() = testScope.runTest {
        coEvery { localRepository.getNumberOfTicket() } returns flowOf(42)

        val result = repository.getNumberOfTicket().first()

        assertThat(result).isEqualTo(42)
        coVerify(exactly = 1) { localRepository.getNumberOfTicket() }
        coVerify(exactly = 0) { networkRepository.getNumberOfTicket() }
    }

    @Test
    fun `getNumberOfTicket returns network flow when connected`() = testScope.runTest {
        setToken("dummy-token")
        coEvery { networkRepository.getNumberOfTicket() } returns flowOf(7)

        val result = repository.getNumberOfTicket().first()

        assertThat(result).isEqualTo(7)
        coVerify(exactly = 1) { networkRepository.getNumberOfTicket() }
        coVerify(exactly = 0) { localRepository.getNumberOfTicket() }
    }
}
