package kztproject.jp.splacounter.database.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class Reward(@PrimaryKey(autoGenerate = true) var id: Int,
                  var name: String,
                  var consumePoint: Int,
                  var description: String?,
                  var needRepeat: Boolean) {

    @Ignore
    constructor(): this("", 0, null, false)

    @Ignore
    constructor(name:String, consumePoint: Int, description: String?, needRepeat: Boolean) :
            this(0, name, consumePoint, description, needRepeat)

    @Ignore
    var isSelected: Boolean = false
}