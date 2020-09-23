package kztproject.jp.splacounter.presentation.todo.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.presentation.todo.TodoListFragment

@Module
abstract class TodoModule {

    @ContributesAndroidInjector(modules = [TodoListFragmentModule::class])
    abstract fun contributeTodoListFragment(): TodoListFragment
}