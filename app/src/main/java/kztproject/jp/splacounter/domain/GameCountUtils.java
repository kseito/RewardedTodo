package kztproject.jp.splacounter.domain;

import kztproject.jp.splacounter.model.Counter;

/**
 * Created by k-seito on 2016/02/11.
 */
public class GameCountUtils {

    public static final int GAME_UNIT = 2;

    public static int convertGameCountFromCounter(Counter counter) {
        return counter.getCount() / GAME_UNIT;
    }

}
