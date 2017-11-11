package kztproject.jp.splacounter.model

import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("full_name")
    var fullName: String? = null

}
