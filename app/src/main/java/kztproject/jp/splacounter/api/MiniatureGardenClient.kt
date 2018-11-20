package kztproject.jp.splacounter.api

import io.reactivex.Single
import kztproject.jp.splacounter.model.Counter
import kztproject.jp.splacounter.util.GameCountUtils
import javax.inject.Inject

class MiniatureGardenClient @Inject
constructor(private val service: MiniatureGardenService) {

    fun getCounter(userId: Long): Single<Counter> {
        return service.getCounter(userId.toInt())
    }

    fun consumeCounter(userId: Long, point: Int): Single<Counter> {
        return service.consumeCounter(userId.toInt(), GameCountUtils.GAME_UNIT * point)
    }
}
