package kztproject.jp.splacounter.di;

import kztproject.jp.splacounter.activity.BaseActivity;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;
import kztproject.jp.splacounter.view.fragment.RewardAddFragment;
import kztproject.jp.splacounter.view.fragment.RewardFragment;

/**
 * Created by k-seito on 2016/01/24.
 */
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(AuthFragment fragment);

    void inject(PlayFragment fragment);

    void inject(RewardFragment fragment);

    void inject(RewardAddFragment fragment);
}
