package kztproject.jp.splacounter.di;

import javax.inject.Singleton;

import dagger.Component;
import kztproject.jp.splacounter.activity.BaseActivity;
import kztproject.jp.splacounter.view.fragment.AuthFragment;
import kztproject.jp.splacounter.view.fragment.PlayFragment;
import kztproject.jp.splacounter.view.fragment.RewardAddFragment;
import kztproject.jp.splacounter.view.fragment.RewardFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(AuthFragment fragment);

    void inject(PlayFragment fragment);

    void inject(RewardFragment fragment);

    void inject(RewardAddFragment fragment);
}
