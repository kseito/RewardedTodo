package kztproject.jp.splacounter.auth.api.model

import com.google.gson.annotations.SerializedName

class UserResponse {

    @SerializedName("user")
    var user: TodoistUser = TodoistUser()
}
