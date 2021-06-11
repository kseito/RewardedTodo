package jp.kztproject.rewardedtodo.di.todo

import dagger.Binds
import dagger.Module
import jp.kztproject.rewardedtodo.todo.application.*

@Module
interface TodoListFragmentModule {

    @Binds
    fun bindGetTodoListUseCase(useCase: GetTodoListInteractor): GetTodoListUseCase

    @Binds
    fun bindUpdateTodoListUseCase(useCase: UpdateTodoInteractor): UpdateTodoUseCase

    @Binds
    fun bindDeleteTodoListUseCase(useCase: DeleteTodoInteractor): DeleteTodoUseCase

    @Binds
    fun bindCompleteTodoUseCase(useCase: CompleteTodoInteractor): CompleteTodoUseCase
}