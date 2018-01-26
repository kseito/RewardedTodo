package kztproject.jp.splacounter.api;

import javax.inject.Inject;

import io.reactivex.Observable;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.util.GameCountUtils;

public class MiniatureGardenClient {

    private final MiniatureGardenService service;

    @Inject
    public MiniatureGardenClient(MiniatureGardenService service) {
        this.service = service;
    }

    public Observable<Counter> getCounter(int userId) {
        return service.getCounter(userId);
    }

    public Observable<Counter> consumeCounter(int userId) {
        return service.cosumeCounter(userId, GameCountUtils.GAME_UNIT);
    }

    public Observable<Counter> consumeCounter(int userId, int point) {
        return service.cosumeCounter(userId, GameCountUtils.GAME_UNIT * point);
    }
}
