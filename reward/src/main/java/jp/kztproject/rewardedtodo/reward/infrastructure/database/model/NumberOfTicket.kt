package jp.kztproject.rewardedtodo.reward.infrastructure.database.model

import com.google.gson.annotations.SerializedName
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket

data class NumberOfTicket(@SerializedName("point") val value: Int) {
    fun convert(): NumberOfTicket {
        return NumberOfTicket(value)
    }
}