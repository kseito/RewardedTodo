package jp.kztproject.rewardedtodo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.kztproject.rewardedtodo.di.todo.TodoRepositoryModule
import jp.kztproject.rewardedtodo.presentation.todo.TodoListFragment
import jp.kztproject.rewardedtodo.di.todo.TodoListFragmentModule

@Module(includes = [TodoRepositoryModule::class])
abstract class TodoModule {

    @ContributesAndroidInjector(modules = [TodoListFragmentModule::class])
    abstract fun contributeTodoListFragment(): TodoListFragment
}