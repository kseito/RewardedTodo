package kztproject.jp.splacounter.util

import kztproject.jp.splacounter.model.Counter

class GameCountUtils {
    companion object {
        const val GAME_UNIT = 2

        fun convertGameCountFromCounter(counter: Counter): Int {
            return counter.count / GAME_UNIT
        }
    }
}
