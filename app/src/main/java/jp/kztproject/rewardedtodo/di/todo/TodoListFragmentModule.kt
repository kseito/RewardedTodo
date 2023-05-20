package jp.kztproject.rewardedtodo.di.todo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoInteractor
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoInteractor
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListInteractor
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListInteractor
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoInteractor
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase

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
