package kztproject.jp.splacounter.di;

import kztproject.jp.splacounter.activity.AuthActivity;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;

/**
 * Created by k-seito on 2016/01/24.
 */
public interface AppComponent {

    void inject(AuthActivity activity);

    void inject(AuthFragment fragment);

    void inject(PlayFragment fragment);
}
