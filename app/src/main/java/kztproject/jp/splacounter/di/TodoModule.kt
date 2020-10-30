package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.di.todo.TodoRepositoryModule
import kztproject.jp.splacounter.presentation.todo.TodoListFragment
import kztproject.jp.splacounter.di.todo.TodoListFragmentModule

@Module(includes = [TodoRepositoryModule::class])
abstract class TodoModule {

    @ContributesAndroidInjector(modules = [TodoListFragmentModule::class])
    abstract fun contributeTodoListFragment(): TodoListFragment
}