package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.model.Counter
import kztproject.jp.splacounter.util.GameCountUtils
import javax.inject.Inject

class MiniatureGardenClient @Inject
constructor(private val service: MiniatureGardenService) {

    fun getCounter(userId: Int): Single<Counter> {
        return service.getCounter(userId)
    }

    fun consumeCounter(userId: Int): Single<Counter> {
        return service.consumeCounter(userId, GameCountUtils.GAME_UNIT)
    }

    fun consumeCounter(userId: Int, point: Int): Single<Counter> {
        return service.consumeCounter(userId, GameCountUtils.GAME_UNIT * point)
    }
}
