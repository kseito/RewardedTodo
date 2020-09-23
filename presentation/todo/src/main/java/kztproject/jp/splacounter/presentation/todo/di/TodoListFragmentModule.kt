package kztproject.jp.splacounter.presentation.todo.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.presentation.todo.TodoListViewModel
import project.seito.screen_transition.di.ViewModelKey

@Module
interface TodoListFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: TodoListViewModel): ViewModel
}