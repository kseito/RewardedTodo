package jp.kztproject.rewardedtodo.di.todo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.kztproject.rewardedtodo.todo.application.*

@InstallIn(ActivityRetainedComponent::class)
@Module
interface TodoListFragmentModule {

    @Binds
    fun bindGetTodoListUseCase(useCase: GetTodoListInteractor): GetTodoListUseCase

    @Binds
    fun bindFetchTodoListUseCase(useCase: FetchTodoListInteractor): FetchTodoListUseCase

    @Binds
    fun bindUpdateTodoListUseCase(useCase: UpdateTodoInteractor): UpdateTodoUseCase

    @Binds
    fun bindDeleteTodoListUseCase(useCase: DeleteTodoInteractor): DeleteTodoUseCase

    @Binds
    fun bindCompleteTodoUseCase(useCase: CompleteTodoInteractor): CompleteTodoUseCase
}