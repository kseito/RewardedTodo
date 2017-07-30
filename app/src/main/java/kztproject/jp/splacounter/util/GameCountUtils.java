package kztproject.jp.splacounter.util;

import kztproject.jp.splacounter.model.Counter;

public class GameCountUtils {

    public static final int GAME_UNIT = 2;

    public static int convertGameCountFromCounter(Counter counter) {
        return counter.getCount() / GAME_UNIT;
    }

}
