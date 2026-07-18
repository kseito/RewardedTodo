package jp.kztproject.rewardedtodo.data.ticket

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class LocalTicketRepositoryTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testScope = TestScope()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: LocalTicketRepository

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_ticket.preferences_pb") },
        )
        repository = LocalTicketRepository(dataStore)
    }

    @Test
    fun `getNumberOfTicket returns 0 when no tickets exist`() = testScope.runTest {
        val result = repository.getNumberOfTicket().first()

        result shouldBe 0
    }

    @Test
    fun `addTicket increases ticket count`() = testScope.runTest {
        repository.addTicket(5)

        val result = repository.getNumberOfTicket().first()
        result shouldBe 5
    }

    @Test
    fun `consumeTicket decreases ticket count by 1`() = testScope.runTest {
        repository.addTicket(5)

        repository.consumeTicket()

        val result = repository.getNumberOfTicket().first()
        result shouldBe 4
    }

    @Test(expected = LackOfTicketsException::class)
    fun `consumeTicket throws LackOfTicketsException when no tickets`() = testScope.runTest {
        repository.consumeTicket()
    }

    @Test
    fun `consumeTickets decreases ticket count by specified amount`() = testScope.runTest {
        repository.addTicket(10)

        repository.consumeTickets(3)

        val result = repository.getNumberOfTicket().first()
        result shouldBe 7
    }

    @Test(expected = LackOfTicketsException::class)
    fun `consumeTickets throws LackOfTicketsException when not enough tickets`() = testScope.runTest {
        repository.addTicket(2)
        repository.consumeTickets(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `consumeTickets throws IllegalArgumentException when count is zero`() = testScope.runTest {
        repository.addTicket(5)
        repository.consumeTickets(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `consumeTickets throws IllegalArgumentException when count is negative`() = testScope.runTest {
        repository.addTicket(5)
        repository.consumeTickets(-1)
    }
}
