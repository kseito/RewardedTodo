package jp.kztproject.rewardedtodo.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap
import jp.kztproject.rewardedtodo.di.reward.RewardDatabaseModule
import jp.kztproject.rewardedtodo.di.reward.RewardModule
import jp.kztproject.rewardedtodo.di.scope.ActivityScope
import jp.kztproject.rewardedtodo.di.todo.TodoDatabaseModule
import jp.kztproject.rewardedtodo.presentation.HomeActivity
import jp.kztproject.rewardedtodo.presentation.HomeViewModel
import project.seito.screen_transition.di.ViewModelKey

@InstallIn(ActivityRetainedComponent::class)
@Module(includes = [
    RewardModule::class,
    TodoModule::class,
    RewardDatabaseModule::class,
    TodoDatabaseModule::class])
internal abstract class HomeActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel
}
