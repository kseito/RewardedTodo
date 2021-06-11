package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.kztproject.rewardedtodo.di.reward.RewardDatabaseModule
import jp.kztproject.rewardedtodo.di.reward.RewardModule
import jp.kztproject.rewardedtodo.di.todo.TodoDatabaseModule

@InstallIn(ActivityRetainedComponent::class)
@Module(includes = [
    RewardModule::class,
    TodoModule::class,
    RewardDatabaseModule::class,
    TodoDatabaseModule::class])
internal abstract class HomeActivityModule
