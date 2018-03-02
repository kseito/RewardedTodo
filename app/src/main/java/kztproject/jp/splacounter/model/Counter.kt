package kztproject.jp.splacounter.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Counter {

    @SerializedName("id")
    @Expose
    var id: Int = 0
    @SerializedName("count")
    @Expose
    var count: Int = 0
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

}
