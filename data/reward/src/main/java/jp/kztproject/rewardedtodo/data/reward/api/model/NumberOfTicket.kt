package jp.kztproject.rewardedtodo.data.reward.api.model

import com.google.gson.annotations.SerializedName
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket

data class NumberOfTicket(@SerializedName("point") val value: Int) {
    fun convert(): NumberOfTicket {
        return NumberOfTicket(value)
    }
}