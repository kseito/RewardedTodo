package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.database.model.Reward
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

class LotteryInteractor @Inject constructor() : LotteryUseCase {
    override suspend fun execute(rewards: List<Reward>): Reward? {
        rewards.forEach {
            val denominator: Int = (100 / it.probability).roundToInt()
            val result = Random.nextInt(denominator)
            if (result == 0) {
                return it
            }
        }
        return null
    }
}