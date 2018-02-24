package kztproject.jp.splacounter.viewmodel;

import android.databinding.ObservableField;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.api.MiniatureGardenClient;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.preference.PrefsWrapper;
import kztproject.jp.splacounter.util.GameCountUtils;

public class PlayViewModel {

    private MiniatureGardenClient serviceClient;
    private Callback callback;
    private ObservableField<Integer> gameCount = new ObservableField<>();

    @Inject
    public PlayViewModel(MiniatureGardenClient serviceClient) {
        this.serviceClient = serviceClient;
        gameCount.set(0);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void getGameCount() {
        Single<Counter> observable = serviceClient.getCounter(PrefsWrapper.INSTANCE.getUserId());
        showCount(observable);
    }

    public void consumeGameCount() {
        Single<Counter> observable = serviceClient.consumeCounter(PrefsWrapper.INSTANCE.getUserId());
        showCount(observable);
    }

    private void showCount(Single<Counter> observable) {

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> callback.showProgressDialog())
                .doOnEvent((counter, throwable) -> callback.dismissProgressDialog())
                .subscribe(
                        counter -> {
                            int count = GameCountUtils.Companion.convertGameCountFromCounter(counter);
                            gameCount.set(count);

                            //TODO remove because of useless
                            callback.showGameCount(count);
                        },
                        e -> callback.showError(e)
                );
    }

    public void showReward() {
        callback.showReward(gameCount.get());
    }

    public interface Callback {
        void showProgressDialog();

        void dismissProgressDialog();

        void showGameCount(int gameCount);

        void showReward(int gameCount);

        void showError(Throwable e);
    }

}
