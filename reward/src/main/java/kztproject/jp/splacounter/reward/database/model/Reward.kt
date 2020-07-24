package kztproject.jp.splacounter.reward.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Reward(@PrimaryKey(autoGenerate = true) var id: Int,
                  var name: String,
                  var consumePoint: Int,
                  var probability: Float,
                  var description: String?,
                  var needRepeat: Boolean) {

    @Ignore
    constructor() : this("", 0, 0F, null, false)

    @Ignore
    constructor(name: String, consumePoint: Int, probability: Float, description: String?, needRepeat: Boolean) :
            this(0, name, consumePoint, probability, description, needRepeat)

    @Ignore
    var isSelected: Boolean = false
}