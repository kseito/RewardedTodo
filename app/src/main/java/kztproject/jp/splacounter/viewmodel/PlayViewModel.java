package kztproject.jp.splacounter.viewmodel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kztproject.jp.splacounter.api.MyServiceClient;
import kztproject.jp.splacounter.domain.GameCountUtils;
import kztproject.jp.splacounter.model.Counter;
import kztproject.jp.splacounter.preference.AppPrefsProvider;

public class PlayViewModel {

    MyServiceClient serviceClient;

    AppPrefsProvider prefs;

    Callback callback;

    @Inject
    public PlayViewModel(MyServiceClient serviceClient, AppPrefsProvider prefs) {
        this.serviceClient = serviceClient;
        this.prefs = prefs;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void getGameCount() {
        Observable<Counter> observable = serviceClient.getCounter(prefs.get().getUserId());
        showCount(observable);
    }

    public void consumeGameCount() {
        Observable<Counter> observable = serviceClient.consumeCounter(prefs.get().getUserId());
        showCount(observable);
    }

    private void showCount(Observable<Counter> observable) {

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> callback.showProgressDialog())
                .doOnTerminate(() -> callback.dismissProgressDialog())
                .subscribe(
                        counter -> {
                            int count = GameCountUtils.convertGameCountFromCounter(counter);
                            callback.countDownGameCount(count);
                        },
                        e -> callback.showError(e)
                );
    }

    public interface Callback {
        void showProgressDialog();

        void dismissProgressDialog();

        void countDownGameCount(int gameCount);

        void showError(Throwable e);
    }

}
