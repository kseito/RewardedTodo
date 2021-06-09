package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.kztproject.rewardedtodo.di.auth.TodoistApiModule

@InstallIn(ActivityRetainedComponent::class)
@Module(includes = [TodoistApiModule::class])
interface TodoistAuthActivityModule