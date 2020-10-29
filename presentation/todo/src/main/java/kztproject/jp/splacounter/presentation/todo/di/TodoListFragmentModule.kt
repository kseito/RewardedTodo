package kztproject.jp.splacounter.presentation.todo.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.presentation.todo.TodoListViewModel
import kztproject.jp.splacounter.todo.application.GetTodoListInteractor
import kztproject.jp.splacounter.todo.application.GetTodoListUseCase
import kztproject.jp.splacounter.todo.application.UpdateTodoInteractor
import kztproject.jp.splacounter.todo.application.UpdateTodoUseCase
import project.seito.screen_transition.di.ViewModelKey

@Module
interface TodoListFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    fun bindVideoTrimMuteViewModel(viewModel: TodoListViewModel): ViewModel

    @Binds
    fun bindGetTodoListUseCase(useCase: GetTodoListInteractor): GetTodoListUseCase

    @Binds
    fun bindUpdateTodoListUseCase(useCase: UpdateTodoInteractor): UpdateTodoUseCase
}