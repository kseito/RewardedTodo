package kztproject.jp.splacounter.data.ticket

import android.content.SharedPreferences
import javax.inject.Inject

class TicketRepository @Inject constructor(private val preferences: SharedPreferences) : ITicketRepository {

    companion object {
        private const val NUMBER_OF_TICKET = "number_of_ticket"
    }

    override fun addTicket(numberOfTicket: Float) {
        val current = getNumberOfTicket()
        preferences.edit()
                .putFloat(NUMBER_OF_TICKET, current + numberOfTicket)
                .apply()
    }

    override fun getNumberOfTicket(): Float {
        return preferences.getFloat(NUMBER_OF_TICKET, 0F)
    }
}