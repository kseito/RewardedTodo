package kztproject.jp.splacounter.di.todo

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kztproject.jp.splacounter.presentation.todo.TodoListViewModel
import kztproject.jp.splacounter.todo.application.*
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

    @Binds
    fun bindDeleteTodoListUseCase(useCase: DeleteTodoInteractor): DeleteTodoUseCase

    @Binds
    fun bindCompleteTodoUseCase(useCase: CompleteTodoInteractor): CompleteTodoUseCase
}