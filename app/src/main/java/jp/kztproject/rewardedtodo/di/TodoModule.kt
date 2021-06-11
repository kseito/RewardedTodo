package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.kztproject.rewardedtodo.di.todo.TodoRepositoryModule
import jp.kztproject.rewardedtodo.presentation.todo.TodoListFragment
import jp.kztproject.rewardedtodo.di.todo.TodoListFragmentModule

@Module(includes = [TodoRepositoryModule::class, TodoListFragmentModule::class])
abstract class TodoModule