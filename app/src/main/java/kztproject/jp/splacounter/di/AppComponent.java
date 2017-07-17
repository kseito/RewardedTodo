package kztproject.jp.splacounter.di;

import kztproject.jp.splacounter.AuthFragment;
import kztproject.jp.splacounter.activity.AuthActivity;
import kztproject.jp.splacounter.activity.MainActivity;

/**
 * Created by k-seito on 2016/01/24.
 */
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(AuthActivity activity);

    void inject(AuthFragment fragment);
}
