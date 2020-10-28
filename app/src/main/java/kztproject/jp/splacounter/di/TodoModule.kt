package kztproject.jp.splacounter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kztproject.jp.splacounter.data.todo.di.TodoDatabaseModule
import kztproject.jp.splacounter.data.todo.di.TodoRepositoryModule
import kztproject.jp.splacounter.presentation.todo.TodoListFragment
import kztproject.jp.splacounter.presentation.todo.di.TodoListFragmentModule

@Module(includes = [TodoRepositoryModule::class])
abstract class TodoModule {

    @ContributesAndroidInjector(modules = [TodoListFragmentModule::class])
    abstract fun contributeTodoListFragment(): TodoListFragment
}