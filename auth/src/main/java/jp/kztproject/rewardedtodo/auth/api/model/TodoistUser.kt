package jp.kztproject.rewardedtodo.auth.api.model

import com.google.gson.annotations.SerializedName

class TodoistUser {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("full_name")
    var fullName: String? = null

}
