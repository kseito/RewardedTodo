package kztproject.jp.splacounter.data.ticket

import android.content.SharedPreferences

class TicketRepository constructor(private val preferences: SharedPreferences) : ITicketRepository {

    companion object {
        private const val NUMBER_OF_TICKET = "number_of_ticket"
    }

    override fun addTicket(numberOfTicket: Float) {
        preferences.edit()
                .putFloat(NUMBER_OF_TICKET, numberOfTicket)
                .apply()
    }

    override fun getNumberOfTicket(): Float {
        return preferences.getFloat(NUMBER_OF_TICKET, 0F)
    }
}