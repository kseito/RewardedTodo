package jp.kztproject.rewardedtodo.data.ticket

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class TicketRepository @Inject constructor(
        @Named("default") private val preferences: SharedPreferences,
        private val datastore: DataStore<Preferences>
) : ITicketRepository {

    companion object {
        private const val NUMBER_OF_TICKET = "number_of_ticket"
        //TODO put into domain layer
        private const val NUMBER_OF_TICKETS_REQUIRED_FOR_LOTTERY = 1
    }

    override suspend fun addTicket(numberOfTicket: Float) {
        datastore.edit { settings ->
            val numberOfTicketKey = floatPreferencesKey(NUMBER_OF_TICKET)
            val currentNumberOfTicket = settings[numberOfTicketKey] ?: 0f
            settings[numberOfTicketKey] = currentNumberOfTicket + numberOfTicket
        }
    }

    override fun consumeTicket() {
        val current = 0f //FIXME
        if (current < NUMBER_OF_TICKETS_REQUIRED_FOR_LOTTERY) {
            throw LackOfTicketsException()
        }
        preferences.edit()
                .putFloat(NUMBER_OF_TICKET, current - NUMBER_OF_TICKETS_REQUIRED_FOR_LOTTERY)
                .apply()
    }

    override suspend fun getNumberOfTicket(): Flow<Float> {
        return datastore.data.map {preferences ->
            val numberOfTicket = floatPreferencesKey(NUMBER_OF_TICKET)
            preferences[numberOfTicket] ?: 0f
        }
    }
}
