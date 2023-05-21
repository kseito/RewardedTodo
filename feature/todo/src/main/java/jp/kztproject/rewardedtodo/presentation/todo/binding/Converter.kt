package jp.kztproject.rewardedtodo.presentation.todo.binding

import androidx.databinding.InverseMethod

object Converter {
    @InverseMethod("stringToNumberOfTickets")
    @JvmStatic
    fun numberOfTicketsToString(value: Float): String {
        return value.toString()
    }

    @JvmStatic
    fun stringToNumberOfTickets(value: String): Float {
        return try {
            value.toFloat()
        } catch (e: Exception) {
            0f
        }
    }
}
