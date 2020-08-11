package kztproject.jp.splacounter.reward.infrastructure.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kztproject.jp.splacounter.reward.domain.model.Probability
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardId

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

    fun convert(): Reward {
        return Reward(
                RewardId(this.id),
                this.name,
                this.consumePoint,
                Probability(this.probability),
                this.description,
                this.needRepeat
        )
    }

    companion object {
        fun from(reward: Reward): kztproject.jp.splacounter.reward.infrastructure.database.model.Reward {
            return Reward(
                    reward.rewardId.value,
                    reward.name,
                    reward.consumePoint,
                    reward.probability.value,
                    reward.description,
                    reward.needRepeat
            )
        }
    }
}