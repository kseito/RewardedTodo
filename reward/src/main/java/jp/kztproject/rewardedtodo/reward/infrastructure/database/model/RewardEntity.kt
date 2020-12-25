package jp.kztproject.rewardedtodo.reward.infrastructure.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import jp.kztproject.rewardedtodo.reward.domain.model.*
import jp.kztproject.rewardedtodo.reward.domain.model.RewardInput

@Entity
data class RewardEntity(@PrimaryKey(autoGenerate = true) var id: Int,
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

    fun convert(): Reward {
        return Reward(
                RewardId(this.id),
                RewardName(this.name),
                this.consumePoint,
                Probability(this.probability),
                RewardDescription(this.description),
                this.needRepeat
        )
    }

    companion object {
        fun from(reward: Reward): RewardEntity {
            return RewardEntity(
                    reward.rewardId.value,
                    reward.name.value,
                    reward.consumePoint,
                    reward.probability.value,
                    reward.description.value,
                    reward.needRepeat
            )
        }

        fun from(rewardInput: RewardInput): RewardEntity {
            if (rewardInput.id == null) {
                return RewardEntity(
                        rewardInput.name!!,
                        rewardInput.consumePoint!!,
                        rewardInput.probability!!,
                        rewardInput.description,
                        rewardInput.needRepeat
                )
            }

            return RewardEntity(
                    rewardInput.id!!,
                    rewardInput.name!!,
                    rewardInput.consumePoint!!,
                    rewardInput.probability!!,
                    rewardInput.description,
                    rewardInput.needRepeat
            )
        }
    }
}