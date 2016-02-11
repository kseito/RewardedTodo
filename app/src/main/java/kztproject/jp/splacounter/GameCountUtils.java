package kztproject.jp.splacounter;

import kztproject.jp.splacounter.model.Counter;

/**
 * Created by k-seito on 2016/02/11.
 */
public class GameCountUtils {

    public static final int GAME_UNIT = 4;

    public static int convertGameCountFromCounter(Counter counter) {
        return counter.getCount() / GAME_UNIT;
    }

}
